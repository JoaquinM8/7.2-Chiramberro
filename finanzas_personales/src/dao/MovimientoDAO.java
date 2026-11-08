package dao;

import conexion.ConexionBD;
import modelo.GastoCategoria;
import modelo.Movimiento;
import modelo.ResumenDiario;
import modelo.ResumenMensual;
import modelo.ResumenTotales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DAO (Data Access Object) de la tabla {@code movimientos}.
 *
 * Concentra todo el SQL del proyecto. La interfaz/consola nunca escribe SQL:
 * solo llama a estos metodos y recibe objetos {@link Movimiento}, resumenes o
 * booleanos.
 *
 * Decisiones de diseno:
 *
 * <ul>
 *   <li>Todas las consultas usan {@link PreparedStatement}. Los valores viajan
 *       como marcadores {@code ?}, nunca concatenados: asi se previene la
 *       inyeccion SQL y se reutiliza el plan de ejecucion.</li>
 *   <li>Todos los recursos (Connection, PreparedStatement, ResultSet) se
 *       cierran con try-with-resources.</li>
 *   <li>El importe se envia con {@code setBigDecimal} para conservar los dos
 *       decimales exactos de {@code DECIMAL(12,2)}.</li>
 *   <li>Las fechas se convierten de {@link LocalDate} a {@link java.sql.Date}
 *       con {@code Date.valueOf(...)}.</li>
 *   <li>Las consultas de analisis se resuelven en MySQL con SUM, CASE,
 *       GROUP BY, JOIN y rangos de fechas; Java solo transporta el
 *       resultado.</li>
 * </ul>
 */
public class MovimientoDAO {

    /* =============================================================
       1) SQL REUTILIZABLE
       ============================================================= */

    /** Columnas que se leen en todos los SELECT. */
    private static final String COLUMNAS =
            "id_movimiento, id_cuenta, id_categoria, tipo, fecha, importe, "
          + "descripcion, medio_pago, es_recurrente";

    private static final String SELECT_BASE =
            "SELECT " + COLUMNAS + " FROM movimientos";

    private static final String SQL_INSERTAR =
            "INSERT INTO movimientos "
          + "(id_cuenta, id_categoria, tipo, fecha, importe, descripcion, medio_pago, es_recurrente) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE movimientos SET id_cuenta = ?, id_categoria = ?, tipo = ?, "
          + "fecha = ?, importe = ?, descripcion = ?, medio_pago = ?, "
          + "es_recurrente = ? WHERE id_movimiento = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM movimientos WHERE id_movimiento = ?";

    /* =============================================================
       2) CONSULTAS DE LECTURA
       ============================================================= */

    /**
     * Devuelve todos los movimientos ordenados por fecha descendente
     * (el mas reciente primero). Ante fechas iguales, ordena por id.
     *
     * @return lista con todos los registros; vacia si la tabla no tiene filas.
     * @throws SQLException si falla la conexion o la consulta.
     */
    public List<Movimiento> listarTodos() throws SQLException {
        String sql = SELECT_BASE + " ORDER BY fecha DESC, id_movimiento DESC";
        return consultar(sql, null);
    }

    /**
     * Lista las cuentas activas para cargar un combo en la vista.
     *
     * <p>La vista no ejecuta SQL: recibe filas {@code [id, nombre]} y las
     * convierte en opciones visuales. Se mantiene el id separado del texto para
     * no depender de como se muestra el nombre.</p>
     *
     * @return lista de cuentas activas ordenadas por nombre.
     * @throws SQLException si falla la base.
     */
    public List<Object[]> listarCuentasActivas() throws SQLException {
        String sql = "SELECT id_cuenta, nombre FROM cuentas "
                   + "WHERE activa = 1 ORDER BY nombre";
        return consultarOpciones(sql, null);
    }

    /**
     * Lista todas las categorias. La vista la usa para construir un mapa de
     * nombres y mostrar descripciones legibles en la JTable.
     *
     * @return lista de categorias ordenadas por nombre.
     * @throws SQLException si falla la base.
     */
    public List<Object[]> listarCategorias() throws SQLException {
        String sql = "SELECT id_categoria, nombre FROM categorias ORDER BY nombre";
        return consultarOpciones(sql, null);
    }

    /**
     * Lista las categorias compatibles con un tipo de movimiento.
     *
     * <p>Incluye las categorias {@code AMBOS} y las del tipo indicado. Se usa
     * para filtrar el combo de categorias y evitar que el usuario elija una
     * combinacion invalida.</p>
     *
     * @param tipo 'INGRESO' o 'GASTO'.
     * @return lista de categorias compatibles.
     * @throws IllegalArgumentException si el tipo no es valido.
     * @throws SQLException              si falla la base.
     */
    public List<Object[]> listarCategoriasPorTipo(String tipo) throws SQLException {
        String tipoNormalizado = normalizarTipo(tipo);

        String sql = "SELECT id_categoria, nombre FROM categorias "
                   + "WHERE tipo_permitido = 'AMBOS' OR tipo_permitido = ? "
                   + "ORDER BY nombre";

        return consultarOpciones(sql, ps -> ps.setString(1, tipoNormalizado));
    }

    /**
     * Devuelve los movimientos de un tipo ('INGRESO' o 'GASTO').
     * El tipo se normaliza a mayusculas, por lo que tambien acepta "gasto".
     *
     * @param tipo tipo de movimiento a filtrar.
     * @return lista filtrada, ordenada por fecha descendente.
     * @throws IllegalArgumentException si el tipo es nulo o invalido.
     * @throws SQLException              si falla la base.
     */
    public List<Movimiento> listarPorTipo(String tipo) throws SQLException {
        String tipoNormalizado = normalizarTipo(tipo);

        String sql = SELECT_BASE
                   + " WHERE tipo = ? "
                   + " ORDER BY fecha DESC, id_movimiento DESC";

        return consultar(sql, ps -> ps.setString(1, tipoNormalizado));
    }

    /**
     * Busca movimientos con fecha dentro del rango indicado, con ambos
     * extremos incluidos (BETWEEN es inclusivo). Se ordena cronologicamente.
     *
     * @param desde fecha inicial (no nula).
     * @param hasta fecha final (no nula y mayor o igual a {@code desde}).
     * @return movimientos del rango; vacia si no hay coincidencias.
     * @throws IllegalArgumentException si alguna fecha es nula o el rango
     *                                  esta invertido.
     * @throws SQLException              si falla la base.
     */
    public List<Movimiento> buscarPorFecha(LocalDate desde, LocalDate hasta) throws SQLException {
        validarRango(desde, hasta);

        String sql = SELECT_BASE
                   + " WHERE fecha BETWEEN ? AND ? "
                   + " ORDER BY fecha ASC, id_movimiento ASC";

        return consultar(sql, ps -> {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
        });
    }

    /**
     * Variante con rango semAbierto: {@code desde} inclusive y {@code hasta}
     * exclusivo ({@code fecha >= desde AND fecha < hasta}).
     *
     * Es la forma recomendada cuando la columna incluyera hora, porque evita
     * perder los registros del ultimo dia. Para consultar todo marzo 2026 se
     * pasa {@code desde = 2026-03-01} y {@code hasta = 2026-04-01}.
     *
     * @param desde fecha inicial incluida (no nula).
     * @param hasta fecha final excluida (no nula y no anterior a {@code desde}).
     * @return movimientos del rango; vacia si no hay coincidencias.
     * @throws IllegalArgumentException si alguna fecha es nula o el rango
     *                                  esta invertido.
     * @throws SQLException              si falla la base.
     */
    public List<Movimiento> buscarPorFechaExclusiva(LocalDate desde, LocalDate hasta)
            throws SQLException {
        validarRango(desde, hasta);

        String sql = SELECT_BASE
                   + " WHERE fecha >= ? AND fecha < ? "
                   + " ORDER BY fecha ASC, id_movimiento ASC";

        return consultar(sql, ps -> {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
        });
    }

    /**
     * Busca un movimiento por su identificador.
     *
     * @param id identificador del movimiento (mayor que cero).
     * @return el movimiento encontrado o {@code null} si no existe.
     * @throws IllegalArgumentException si el id no es valido.
     * @throws SQLException              si falla la base.
     */
    public Movimiento buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de movimiento invalido: " + id);
        }

        String sql = SELECT_BASE + " WHERE id_movimiento = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMovimiento(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lista movimientos de un tipo dentro de un periodo con limite superior
     * exclusivo. Para marzo 2026: {@code desde = 2026-03-01} y
     * {@code hastaExclusiva = 2026-04-01}.
     *
     * @param tipo           'INGRESO' o 'GASTO' (se normaliza a mayusculas).
     * @param desde          primer dia incluido.
     * @param hastaExclusiva primer dia del mes siguiente (excluido).
     * @return movimientos del tipo y periodo, ordenados por fecha descendente.
     * @throws IllegalArgumentException si el tipo o el rango no son validos.
     * @throws SQLException              si falla la base.
     */
    public List<Movimiento> listarPorTipoYFechas(String tipo, LocalDate desde,
                                                 LocalDate hastaExclusiva)
            throws SQLException {
        String tipoNormalizado = normalizarTipo(tipo);
        validarRangoExclusivo(desde, hastaExclusiva);

        String sql = SELECT_BASE
                   + " WHERE tipo = ? AND fecha >= ? AND fecha < ? "
                   + " ORDER BY fecha DESC, id_movimiento DESC";

        return consultar(sql, ps -> {
            ps.setString(1, tipoNormalizado);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hastaExclusiva));
        });
    }

    /**
     * Busca movimientos cuyo texto de descripcion contenga el valor indicado.
     * El patron se envia como parametro: el SQL permanece fijo y el valor no
     * se concatena dentro de la consulta.
     *
     * @param texto texto a buscar (no nulo).
     * @return movimientos coincidentes, ordenados por fecha descendente.
     * @throws IllegalArgumentException si el texto es nulo.
     * @throws SQLException              si falla la base.
     */
    public List<Movimiento> buscarPorDescripcion(String texto) throws SQLException {
        if (texto == null) {
            throw new IllegalArgumentException("El texto de busqueda es obligatorio.");
        }

        String sql = SELECT_BASE
                   + " WHERE descripcion LIKE ? "
                   + " ORDER BY fecha DESC, id_movimiento DESC";

        String patron = "%" + texto.trim() + "%";
        return consultar(sql, ps -> ps.setString(1, patron));
    }

    /**
     * Devuelve los GASTOS cuyo importe supera el minimo recibido.
     * El filtro es estricto: importe &gt; minimo.
     *
     * @param importeMinimo monto de corte; no nulo y no negativo.
     * @return gastos ordenados de mayor a menor importe.
     * @throws IllegalArgumentException si el monto es nulo o negativo.
     * @throws SQLException              si falla la base.
     */
    public List<Movimiento> listarGastosMayoresA(BigDecimal importeMinimo) throws SQLException {
        if (importeMinimo == null || importeMinimo.signum() < 0) {
            throw new IllegalArgumentException(
                    "El importe minimo es obligatorio y no puede ser negativo.");
        }

        String sql = SELECT_BASE
                   + " WHERE tipo = 'GASTO' AND importe > ? "
                   + " ORDER BY importe DESC, fecha DESC";

        return consultar(sql, ps -> ps.setBigDecimal(1, importeMinimo));
    }

    /**
     * Calcula en MySQL los totales de ingresos y gastos con
     * {@code SUM(CASE WHEN ...)}.
     *
     * Se hace en la base y no en Java para que el motor recorra las filas una
     * sola vez y no haya que traer todos los movimientos al cliente.
     *
     * @return resumen con cantidades, totales y balance.
     * @throws SQLException si falla la base.
     */
    public ResumenTotales obtenerTotales() throws SQLException {
        String sql =
                "SELECT "
              + "  COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END), 0) AS total_ingresos, "
              + "  COALESCE(SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END), 0) AS total_gastos, "
              + "  COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN 1 ELSE 0 END), 0)       AS cantidad_ingresos, "
              + "  COALESCE(SUM(CASE WHEN tipo = 'GASTO'   THEN 1 ELSE 0 END), 0)       AS cantidad_gastos "
              + "FROM movimientos";

        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new ResumenTotales(
                        rs.getLong("cantidad_ingresos"),
                        rs.getLong("cantidad_gastos"),
                        rs.getBigDecimal("total_ingresos"),
                        rs.getBigDecimal("total_gastos"));
            }
        }
        return new ResumenTotales(0, 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Resumen mensual de ingresos, gastos y resultado.
     *
     * <p>El resultado se calcula sobre movimientos registrados: no incluye
     * saldos iniciales ni movimientos de otros periodos. Si el mes no tiene
     * movimientos, {@code COALESCE} devuelve cero.</p>
     *
     * @param desde          primer dia del periodo (incluido).
     * @param hastaExclusiva primer dia del mes siguiente (excluido).
     * @return ingresos, gastos y resultado del periodo.
     * @throws IllegalArgumentException si el rango no es valido.
     * @throws SQLException              si falla la base.
     */
    public ResumenMensual resumenMensual(LocalDate desde, LocalDate hastaExclusiva)
            throws SQLException {
        validarRangoExclusivo(desde, hastaExclusiva);

        String sql =
                "SELECT "
              + "  COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END), 0) AS ingresos, "
              + "  COALESCE(SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END), 0) AS gastos, "
              + "  COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE -importe END), 0) AS resultado "
              + "FROM movimientos "
              + "WHERE fecha >= ? AND fecha < ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hastaExclusiva));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ResumenMensual(
                            rs.getBigDecimal("ingresos"),
                            rs.getBigDecimal("gastos"),
                            rs.getBigDecimal("resultado"));
                }
            }
        }
        return new ResumenMensual(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Gastos agrupados por categoria del periodo, ordenados de mayor a menor.
     *
     * <p>La suma responde cuanto se gasto en cada categoria y
     * {@code COUNT(*)} indica cuantos movimientos contribuyeron al total.</p>
     *
     * @param desde          primer dia del periodo (incluido).
     * @param hastaExclusiva primer dia del mes siguiente (excluido).
     * @return lista de gastos por categoria; vacia si no hubo gastos.
     * @throws IllegalArgumentException si el rango no es valido.
     * @throws SQLException              si falla la base.
     */
    public List<GastoCategoria> gastosPorCategoria(LocalDate desde, LocalDate hastaExclusiva)
            throws SQLException {
        validarRangoExclusivo(desde, hastaExclusiva);

        String sql =
                "SELECT c.id_categoria, c.nombre, "
              + "       SUM(m.importe) AS total_gastado, "
              + "       COUNT(*) AS cantidad_movimientos "
              + "FROM movimientos m "
              + "JOIN categorias c ON c.id_categoria = m.id_categoria "
              + "WHERE m.tipo = 'GASTO' AND m.fecha >= ? AND m.fecha < ? "
              + "GROUP BY c.id_categoria, c.nombre "
              + "ORDER BY total_gastado DESC";

        List<GastoCategoria> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hastaExclusiva));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new GastoCategoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("total_gastado"),
                            rs.getLong("cantidad_movimientos")));
                }
            }
        }
        return lista;
    }

    /**
     * Gastos por categoria con el porcentaje que representa cada una sobre el
     * total de gastos del periodo.
     *
     * <p>El porcentaje se calcula en MySQL. {@code NULLIF} evita la division
     * por cero cuando no existen gastos.</p>
     *
     * @param desde          primer dia del periodo (incluido).
     * @param hastaExclusiva primer dia del mes siguiente (excluido).
     * @return lista con total y porcentaje por categoria.
     * @throws IllegalArgumentException si el rango no es valido.
     * @throws SQLException              si falla la base.
     */
    public List<GastoCategoria> porcentajeGastosPorCategoria(LocalDate desde,
                                                             LocalDate hastaExclusiva)
            throws SQLException {
        validarRangoExclusivo(desde, hastaExclusiva);

        String sql =
                "SELECT c.id_categoria, c.nombre, "
              + "       SUM(m.importe) AS total_gastado, "
              + "       COUNT(*) AS cantidad_movimientos, "
              + "       ROUND(100 * SUM(m.importe) / NULLIF(("
              + "           SELECT SUM(importe) FROM movimientos "
              + "           WHERE tipo = 'GASTO' AND fecha >= ? AND fecha < ?"
              + "       ), 0), 2) AS porcentaje "
              + "FROM movimientos m "
              + "JOIN categorias c ON c.id_categoria = m.id_categoria "
              + "WHERE m.tipo = 'GASTO' AND m.fecha >= ? AND m.fecha < ? "
              + "GROUP BY c.id_categoria, c.nombre "
              + "ORDER BY total_gastado DESC";

        List<GastoCategoria> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hastaExclusiva));
            ps.setDate(3, Date.valueOf(desde));
            ps.setDate(4, Date.valueOf(hastaExclusiva));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new GastoCategoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("total_gastado"),
                            rs.getLong("cantidad_movimientos"),
                            rs.getBigDecimal("porcentaje")));
                }
            }
        }
        return lista;
    }

    /**
     * Ingresos y gastos agrupados por dia dentro del periodo.
     *
     * @param desde          primer dia del periodo (incluido).
     * @param hastaExclusiva primer dia del mes siguiente (excluido).
     * @return lista ordenada cronologicamente; vacia si no hay movimientos.
     * @throws IllegalArgumentException si el rango no es valido.
     * @throws SQLException              si falla la base.
     */
    public List<ResumenDiario> ingresosGastosPorDia(LocalDate desde, LocalDate hastaExclusiva)
            throws SQLException {
        validarRangoExclusivo(desde, hastaExclusiva);

        String sql =
                "SELECT fecha, "
              + "       SUM(CASE WHEN tipo = 'INGRESO' THEN importe ELSE 0 END) AS ingresos, "
              + "       SUM(CASE WHEN tipo = 'GASTO'   THEN importe ELSE 0 END) AS gastos "
              + "FROM movimientos "
              + "WHERE fecha >= ? AND fecha < ? "
              + "GROUP BY fecha "
              + "ORDER BY fecha";

        List<ResumenDiario> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hastaExclusiva));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    lista.add(new ResumenDiario(
                            fecha == null ? null : fecha.toLocalDate(),
                            rs.getBigDecimal("ingresos"),
                            rs.getBigDecimal("gastos")));
                }
            }
        }
        return lista;
    }

    /**
     * Gasto promedio por movimiento de un periodo.
     *
     * <p>Es una consulta de ampliacion: suma los gastos y los divide por la
     * cantidad de movimientos de tipo GASTO.</p>
     *
     * @param desde          primer dia del periodo (incluido).
     * @param hastaExclusiva primer dia del mes siguiente (excluido).
     * @return promedio con dos decimales; cero si no hubo gastos.
     * @throws IllegalArgumentException si el rango no es valido.
     * @throws SQLException              si falla la base.
     */
    public BigDecimal gastoPromedioPorMovimiento(LocalDate desde, LocalDate hastaExclusiva)
            throws SQLException {
        validarRangoExclusivo(desde, hastaExclusiva);

        String sql =
                "SELECT COALESCE(ROUND(AVG(importe), 2), 0) AS gasto_promedio "
              + "FROM movimientos "
              + "WHERE tipo = 'GASTO' AND fecha >= ? AND fecha < ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hastaExclusiva));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("gasto_promedio");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /* =============================================================
       3) OPERACIONES CRUD
       ============================================================= */

    /**
     * Inserta un movimiento nuevo.
     *
     * Valida primero los datos con {@link #validarMovimiento(Movimiento)} para
     * avisar con un IllegalArgumentException claro antes de llegar a MySQL.
     * Luego comprueba, dentro de la misma conexion, que la categoria admita el
     * tipo de movimiento. Si la base acepta el registro, el objeto recibe el
     * id autogenerado.
     *
     * @param movimiento datos a insertar.
     * @return {@code true} si se inserto exactamente una fila.
     * @throws IllegalArgumentException si el movimiento o la categoria no pasan
     *                                  la validacion.
     * @throws SQLException              si falla la conexion, una FK o un CHECK
     *                                   de la base.
     */
    public boolean insertar(Movimiento movimiento) throws SQLException {
        validarMovimiento(movimiento);

        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(
                     SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            // La categoria debe existir y admitir el tipo (INGRESO/GASTO/AMBOS).
            validarCompatibilidadCategoria(cx, movimiento.getIdCategoria(),
                    movimiento.getTipo());

            ps.setInt(1, movimiento.getIdCuenta());
            ps.setInt(2, movimiento.getIdCategoria());
            ps.setString(3, movimiento.getTipo());
            ps.setDate(4, Date.valueOf(movimiento.getFecha()));
            ps.setBigDecimal(5, movimiento.getImporte());
            ps.setString(6, movimiento.getDescripcion().trim());
            ps.setString(7, movimiento.getMedioPago().trim());
            ps.setBoolean(8, movimiento.isRecurrente());

            int filas = ps.executeUpdate();

            if (filas == 1) {
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        movimiento.setIdMovimiento(claves.getInt(1));
                    }
                }
            }
            return filas == 1;
        }
    }

    /**
     * Actualiza un movimiento existente.
     *
     * <p>El {@code WHERE id_movimiento = ?} es obligatorio: sin el se
     * modificarian todas las filas. Devuelve {@code false} si el id no existe
     * o si no se afecto ninguna fila.</p>
     *
     * @param movimiento movimiento con id valido y datos actualizados.
     * @return {@code true} si se actualizo exactamente una fila.
     * @throws IllegalArgumentException si el movimiento o su id no son validos.
     * @throws SQLException              si falla la base.
     */
    public boolean actualizar(Movimiento movimiento) throws SQLException {
        validarMovimiento(movimiento);

        if (movimiento.getIdMovimiento() <= 0) {
            throw new IllegalArgumentException("ID de movimiento invalido.");
        }

        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ACTUALIZAR)) {

            validarCompatibilidadCategoria(cx, movimiento.getIdCategoria(),
                    movimiento.getTipo());

            ps.setInt(1, movimiento.getIdCuenta());
            ps.setInt(2, movimiento.getIdCategoria());
            ps.setString(3, movimiento.getTipo());
            ps.setDate(4, Date.valueOf(movimiento.getFecha()));
            ps.setBigDecimal(5, movimiento.getImporte());
            ps.setString(6, movimiento.getDescripcion().trim());
            ps.setString(7, movimiento.getMedioPago().trim());
            ps.setBoolean(8, movimiento.isRecurrente());
            ps.setInt(9, movimiento.getIdMovimiento());

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Elimina un movimiento por su identificador.
     *
     * <p>El DAO no muestra ventanas ni pide confirmacion: devuelve el
     * resultado y deja que la interfaz informe al usuario.</p>
     *
     * @param id identificador del movimiento.
     * @return {@code true} si se elimino exactamente una fila.
     * @throws IllegalArgumentException si el id no es valido.
     * @throws SQLException              si falla la base.
     */
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID invalido: " + id);
        }

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /* =============================================================
       4) VALIDACION PREVIA
       ============================================================= */

    /**
     * Valida un movimiento antes de intentar el INSERT o el UPDATE.
     *
     * Reglas:
     * <ul>
     *   <li>El objeto no puede ser nulo.</li>
     *   <li>idCuenta e idCategoria deben ser mayores que cero (las FK de la
     *       base exigen que existan; la existencia real la controla MySQL).</li>
     *   <li>La fecha, el importe, la descripcion y el medio de pago son
     *       obligatorios (la tabla los declara NOT NULL).</li>
     *   <li>El importe debe ser mayor que cero (coincide con
     *       {@code CHECK (importe > 0)}).</li>
     *   <li>El tipo solo admite 'INGRESO' o 'GASTO'. Se normaliza a
     *       mayusculas: "gasto" se guarda como "GASTO".</li>
     * </ul>
     *
     * @param movimiento objeto a validar.
     * @throws IllegalArgumentException si alguna regla no se cumple.
     */
    public void validarMovimiento(Movimiento movimiento) {
        if (movimiento == null) {
            throw new IllegalArgumentException("El movimiento no puede ser nulo.");
        }
        if (movimiento.getIdCuenta() <= 0) {
            throw new IllegalArgumentException("Debe indicar una cuenta valida (idCuenta > 0).");
        }
        if (movimiento.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("Debe indicar una categoria valida (idCategoria > 0).");
        }
        if (movimiento.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (movimiento.getImporte() == null) {
            throw new IllegalArgumentException("El importe es obligatorio.");
        }
        if (movimiento.getImporte().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor que cero. Recibido: "
                    + movimiento.getImporte().toPlainString());
        }
        if (esBlanco(movimiento.getDescripcion())) {
            throw new IllegalArgumentException("La descripcion es obligatoria.");
        }
        if (esBlanco(movimiento.getMedioPago())) {
            throw new IllegalArgumentException(
                    "El medio de pago es obligatorio (ej.: Efectivo, Transferencia, Tarjeta de debito).");
        }

        // Valida y deja el tipo canonico ("gasto" -> "GASTO").
        movimiento.setTipo(normalizarTipo(movimiento.getTipo()));
    }

    /**
     * Comprueba que la categoria exista y que su {@code tipo_permitido} sea
     * compatible con el tipo de movimiento.
     *
     * <p>Se llama dentro de la conexion que ya esta abierta para insertar o
     * actualizar, de modo que la validacion y la escritura usen la misma
     * conexion.</p>
     *
     * @param con            conexion abierta.
     * @param idCategoria    categoria a comprobar.
     * @param tipoMovimiento 'INGRESO' o 'GASTO'.
     * @throws IllegalArgumentException si la categoria no existe o no admite
     *                                  el tipo.
     * @throws SQLException             si falla la consulta.
     */
    private void validarCompatibilidadCategoria(Connection con, int idCategoria,
                                                String tipoMovimiento)
            throws SQLException {
        String sql = "SELECT tipo_permitido FROM categorias WHERE id_categoria = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("La categoria no existe.");
                }

                String permitido = rs.getString("tipo_permitido");
                String tipo = normalizarTipo(tipoMovimiento);

                if (!"AMBOS".equals(permitido) && !permitido.equals(tipo)) {
                    throw new IllegalArgumentException(
                            "La categoria no admite el tipo " + tipo
                            + " (tipo permitido: " + permitido + ").");
                }
            }
        }
    }

    /** Valida que el rango de fechas no sea nulo ni este invertido. */
    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(
                    "Las fechas 'desde' y 'hasta' son obligatorias.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "El rango esta invertido: 'desde' (" + desde
                    + ") no puede ser posterior a 'hasta' (" + hasta + ").");
        }
    }

    /**
     * Valida un rango semAbierto: {@code desde} debe ser estrictamente
     * anterior a {@code hastaExclusiva}.
     */
    private void validarRangoExclusivo(LocalDate desde, LocalDate hastaExclusiva) {
        if (desde == null || hastaExclusiva == null) {
            throw new IllegalArgumentException(
                    "Las fechas 'desde' y 'hastaExclusiva' son obligatorias.");
        }
        if (!desde.isBefore(hastaExclusiva)) {
            throw new IllegalArgumentException(
                    "Rango de fechas invalido: 'desde' (" + desde
                    + ") debe ser anterior a 'hastaExclusiva' (" + hastaExclusiva + ").");
        }
    }

    /* =============================================================
       5) METODOS AUXILIARES INTERNOS
       ============================================================= */

    /**
     * Ejecuta un SELECT de dos columnas (id, texto) y devuelve filas
     * {@code Object[]} listas para que la vista construya sus combos.
     *
     * <p>Este metodo mantiene el SQL dentro del DAO: la vista no conoce ni
     * escribe consultas, solo recibe pares id/nombre.</p>
     *
     * @param sql        consulta con dos columnas: id y texto.
     * @param parametros bloque que carga los parametros; null si no hay.
     */
    private List<Object[]> consultarOpciones(String sql, CargaParametros parametros)
            throws SQLException {

        List<Object[]> opciones = new ArrayList<>();

        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(sql)) {

            if (parametros != null) {
                parametros.cargar(ps);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    opciones.add(new Object[] { rs.getInt(1), rs.getString(2) });
                }
            }
        }
        return opciones;
    }

    /**
     * Ejecuta un SELECT y transforma cada fila del ResultSet en un Movimiento.
     *
     * @param sql        consulta completa con sus marcadores ?.
     * @param parametros bloque que carga los parametros; null si no hay.
     */
    private List<Movimiento> consultar(String sql, CargaParametros parametros)
            throws SQLException {

        List<Movimiento> movimientos = new ArrayList<>();

        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(sql)) {

            if (parametros != null) {
                parametros.cargar(ps);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
        }
        return movimientos;
    }

    /** Convierte la fila actual del ResultSet en un objeto Movimiento. */
    private Movimiento mapearMovimiento(ResultSet rs) throws SQLException {
        Movimiento movimiento = new Movimiento();

        movimiento.setIdMovimiento(rs.getInt("id_movimiento"));
        movimiento.setIdCuenta(rs.getInt("id_cuenta"));
        movimiento.setIdCategoria(rs.getInt("id_categoria"));
        movimiento.setTipo(rs.getString("tipo"));

        Date fecha = rs.getDate("fecha");
        movimiento.setFecha(fecha == null ? null : fecha.toLocalDate());

        movimiento.setImporte(rs.getBigDecimal("importe"));
        movimiento.setDescripcion(rs.getString("descripcion"));
        movimiento.setMedioPago(rs.getString("medio_pago"));
        movimiento.setRecurrente(rs.getBoolean("es_recurrente"));

        return movimiento;
    }

    /**
     * Valida el tipo y lo devuelve en mayusculas. Se usa tanto en las altas
     * como en los filtros para que "gasto" y "GASTO" sean equivalentes.
     */
    private String normalizarTipo(String tipo) {
        if (esBlanco(tipo)) {
            throw new IllegalArgumentException("El tipo es obligatorio ('INGRESO' o 'GASTO').");
        }

        String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
        if (!normalizado.equals("INGRESO") && !normalizado.equals("GASTO")) {
            throw new IllegalArgumentException(
                    "Tipo invalido: '" + tipo + "'. Solo se admite 'INGRESO' o 'GASTO'.");
        }
        return normalizado;
    }

    /** Indica si un texto es nulo, vacio o solo contiene espacios. */
    private boolean esBlanco(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Interfaz funcional interna: permite pasar el bloque que carga los
     * parametros de una consulta sin repetir el mismo codigo en cada metodo.
     */
    @FunctionalInterface
    private interface CargaParametros {
        void cargar(PreparedStatement ps) throws SQLException;
    }
}
