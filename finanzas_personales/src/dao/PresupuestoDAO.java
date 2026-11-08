package dao;

import conexion.ConexionBD;
import modelo.ComparacionPresupuesto;
import modelo.Presupuesto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla {@code presupuestos}.
 *
 * <p>Un presupuesto es un plan mensual por categoria. El gasto real no se
 * almacena: se calcula desde {@code movimientos} en la consulta de
 * comparacion. Este DAO valida que la categoria exista y que no sea
 * exclusivamente de INGRESO.</p>
 *
 * <p>Todas las consultas usan {@link PreparedStatement}; los filtros
 * opcionales de la comparacion se agregan como condiciones fijas y sus valores
 * viajan como parametros.</p>
 */
public class PresupuestoDAO {

    /* =============================================================
       1) SQL REUTILIZABLE
       ============================================================= */

    private static final String COLUMNAS =
            "id_presupuesto, id_usuario, id_categoria, anio, mes, "
          + "monto_planificado, observaciones";

    private static final String SELECT_BASE =
            "SELECT " + COLUMNAS + " FROM presupuestos";

    private static final String SQL_INSERTAR =
            "INSERT INTO presupuestos "
          + "(id_usuario, id_categoria, anio, mes, monto_planificado, observaciones) "
          + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE presupuestos SET id_usuario = ?, id_categoria = ?, anio = ?, "
          + "mes = ?, monto_planificado = ?, observaciones = ? "
          + "WHERE id_presupuesto = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM presupuestos WHERE id_presupuesto = ?";

    private static final String SQL_COMPARAR =
            "SELECT p.id_presupuesto, u.nombre AS usuario, cat.nombre AS categoria, "
          + "       p.anio, p.mes, p.monto_planificado, "
          + "       COALESCE(SUM(m.importe), 0) AS gasto_real, "
          + "       p.monto_planificado - COALESCE(SUM(m.importe), 0) AS disponible, "
          + "       CASE "
          + "         WHEN COALESCE(SUM(m.importe), 0) > p.monto_planificado THEN 'EXCEDIDO' "
          + "         WHEN COALESCE(SUM(m.importe), 0) = p.monto_planificado THEN 'AL LIMITE' "
          + "         ELSE 'DENTRO DEL PRESUPUESTO' "
          + "       END AS estado "
          + "FROM presupuestos p "
          + "JOIN usuarios u ON u.id_usuario = p.id_usuario "
          + "JOIN categorias cat ON cat.id_categoria = p.id_categoria "
          + "LEFT JOIN cuentas c ON c.id_usuario = u.id_usuario "
          + "LEFT JOIN movimientos m "
          + "       ON m.id_cuenta = c.id_cuenta "
          + "      AND m.id_categoria = p.id_categoria "
          + "      AND m.tipo = 'GASTO' "
          + "      AND YEAR(m.fecha) = p.anio "
          + "      AND MONTH(m.fecha) = p.mes "
          + "WHERE 1 = 1";

    /* =============================================================
       2) LECTURA
       ============================================================= */

    /**
     * Devuelve todos los presupuestos ordenados por periodo, usuario y
     * categoria.
     *
     * @return lista completa; vacia si no hay presupuestos.
     * @throws SQLException si falla la base.
     */
    public List<Presupuesto> listarTodos() throws SQLException {
        String sql = SELECT_BASE
                   + " ORDER BY anio DESC, mes DESC, id_usuario, id_categoria";
        return consultarPresupuestos(sql, null);
    }

    /**
     * Lista los presupuestos de un usuario para un periodo concreto.
     *
     * @param idUsuario identificador del usuario.
     * @param anio      anio del presupuesto.
     * @param mes       mes (1 a 12).
     * @return presupuestos del periodo; vacia si no hay.
     * @throws IllegalArgumentException si los parametros no son validos.
     * @throws SQLException              si falla la base.
     */
    public List<Presupuesto> listarPorUsuarioYPeriodo(int idUsuario, int anio, int mes)
            throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El id de usuario debe ser mayor que cero.");
        }
        validarPeriodo(anio, mes);

        String sql = SELECT_BASE
                   + " WHERE id_usuario = ? AND anio = ? AND mes = ? "
                   + " ORDER BY id_categoria";

        return consultarPresupuestos(sql, ps -> {
            ps.setInt(1, idUsuario);
            ps.setInt(2, anio);
            ps.setInt(3, mes);
        });
    }

    /**
     * Busca un presupuesto por su identificador.
     *
     * @param id identificador del presupuesto.
     * @return el presupuesto o {@code null} si no existe.
     * @throws IllegalArgumentException si el id no es valido.
     * @throws SQLException              si falla la base.
     */
    public Presupuesto buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de presupuesto invalido: " + id);
        }

        String sql = SELECT_BASE + " WHERE id_presupuesto = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPresupuesto(rs);
                }
            }
        }
        return null;
    }

    /**
     * Compara presupuesto planificado contra gasto real.
     *
     * <p>Si un filtro llega {@code null}, no se aplica. Por ejemplo
     * {@code compararPresupuestos(1, 2026, 3)} devuelve solo los presupuestos
     * de Ana en marzo de 2026.</p>
     *
     * <p>Se usa LEFT JOIN para que un presupuesto aparezca aunque no haya
     * tenido gastos. El gasto real solo suma movimientos de tipo GASTO de las
     * cuentas del usuario, en la categoria y el mes correspondientes.</p>
     *
     * @param idUsuario filtro por usuario; puede ser null.
     * @param anio      filtro por anio; puede ser null.
     * @param mes       filtro por mes; puede ser null.
     * @return comparaciones ordenadas por usuario y categoria.
     * @throws IllegalArgumentException si el mes no esta entre 1 y 12.
     * @throws SQLException              si falla la base.
     */
    public List<ComparacionPresupuesto> compararPresupuestos(Integer idUsuario,
                                                             Integer anio,
                                                             Integer mes)
            throws SQLException {
        if (idUsuario != null && idUsuario <= 0) {
            throw new IllegalArgumentException("El id de usuario debe ser mayor que cero.");
        }
        if (anio != null && (anio < 1900 || anio > 2200)) {
            throw new IllegalArgumentException("El anio no es valido: " + anio);
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12.");
        }

        StringBuilder sql = new StringBuilder(SQL_COMPARAR);
        List<Object> parametros = new ArrayList<>();

        if (idUsuario != null) {
            sql.append(" AND p.id_usuario = ?");
            parametros.add(idUsuario);
        }
        if (anio != null) {
            sql.append(" AND p.anio = ?");
            parametros.add(anio);
        }
        if (mes != null) {
            sql.append(" AND p.mes = ?");
            parametros.add(mes);
        }

        sql.append(" GROUP BY p.id_presupuesto, u.nombre, cat.nombre, ")
           .append("p.anio, p.mes, p.monto_planificado ")
           .append("ORDER BY u.nombre, cat.nombre");

        List<ComparacionPresupuesto> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ComparacionPresupuesto(
                            rs.getInt("id_presupuesto"),
                            rs.getString("usuario"),
                            rs.getString("categoria"),
                            rs.getInt("anio"),
                            rs.getInt("mes"),
                            rs.getBigDecimal("monto_planificado"),
                            rs.getBigDecimal("gasto_real"),
                            rs.getBigDecimal("disponible"),
                            rs.getString("estado")));
                }
            }
        }
        return lista;
    }

    /**
     * Version para JTable: devuelve filas ya preparadas para la vista.
     *
     * <p>Columnas: id, categoria, monto planificado, gasto real, disponible,
     * porcentaje ejecutado y estado. La vista solo formatea los importes; no
     * calcula ni consulta nada por su cuenta.</p>
     *
     * @param idUsuario usuario dueno del presupuesto.
     * @param anio      anio del periodo.
     * @param mes       mes del periodo (1 a 12).
     * @return filas listas para mostrar.
     * @throws IllegalArgumentException si los parametros no son validos.
     * @throws SQLException              si falla la base.
     */
    public List<Object[]> comparar(int idUsuario, int anio, int mes)
            throws SQLException {

        List<Object[]> filas = new ArrayList<>();

        for (ComparacionPresupuesto comparacion
                : compararPresupuestos(idUsuario, anio, mes)) {

            filas.add(new Object[] {
                comparacion.getIdPresupuesto(),
                comparacion.getCategoria(),
                comparacion.getMontoPlanificado(),
                comparacion.getGastoReal(),
                comparacion.getDisponible(),
                comparacion.getPorcentajeEjecutado(),
                comparacion.getEstado()
            });
        }
        return filas;
    }

    /**
     * Lista usuarios para el combo de presupuestos. Devuelve pares
     * {@code [id, nombre]} sin exponer SQL a la vista.
     *
     * @return usuarios ordenados por nombre.
     * @throws SQLException si falla la base.
     */
    public List<Object[]> listarUsuarios() throws SQLException {
        String sql = "SELECT id_usuario, nombre FROM usuarios ORDER BY nombre";
        return consultarOpciones(sql, null);
    }

    /**
     * Lista categorias que admiten presupuesto (GASTO o AMBOS).
     *
     * @return categorias presupuestables ordenadas por nombre.
     * @throws SQLException si falla la base.
     */
    public List<Object[]> listarCategoriasPresupuestables() throws SQLException {
        String sql = "SELECT id_categoria, nombre FROM categorias "
                   + "WHERE tipo_permitido IN ('GASTO', 'AMBOS') "
                   + "ORDER BY nombre";
        return consultarOpciones(sql, null);
    }

    /* =============================================================
       3) ESCRITURA (CRUD)
       ============================================================= */

    /**
     * Guarda o actualiza un presupuesto mensual.
     *
     * <p>Aprovecha la restriccion UNIQUE
     * {@code (id_usuario, id_categoria, anio, mes)}: si el presupuesto ya
     * existe, actualiza monto y observaciones en lugar de duplicarlo. Es la
     * operacion que usa el boton "Guardar presupuesto" de la interfaz.</p>
     *
     * @param idUsuario   usuario dueno del presupuesto.
     * @param idCategoria categoria de gasto o ambigua.
     * @param anio        anio del periodo.
     * @param mes         mes del periodo (1 a 12).
     * @param monto       monto planificado, no negativo.
     * @param observaciones texto libre; puede ser null.
     * @return {@code true} si la sentencia se ejecuto correctamente.
     * @throws IllegalArgumentException si los datos o la categoria no son
     *                                  validos.
     * @throws SQLException              si falla la base.
     */
    public boolean guardar(int idUsuario, int idCategoria, int anio, int mes,
                           BigDecimal monto, String observaciones)
            throws SQLException {

        Presupuesto presupuesto = new Presupuesto(
                idUsuario, idCategoria, anio, mes, monto, observaciones);
        validarPresupuesto(presupuesto);

        String sql = "INSERT INTO presupuestos "
                   + "(id_usuario, id_categoria, anio, mes, monto_planificado, observaciones) "
                   + "VALUES (?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE "
                   + "monto_planificado = VALUES(monto_planificado), "
                   + "observaciones = VALUES(observaciones)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            validarCategoriaPresupuestable(con, idCategoria);

            ps.setInt(1, idUsuario);
            ps.setInt(2, idCategoria);
            ps.setInt(3, anio);
            ps.setInt(4, mes);
            ps.setBigDecimal(5, monto);
            ps.setString(6, recortarObservaciones(observaciones));

            ps.executeUpdate();

            // Un UPDATE sin cambios puede informar 0 filas; la operacion igual
            // fue exitosa. Solo una excepcion indica que no se guardo.
            return true;
        }
    }

    /**
     * Inserta un presupuesto y devuelve el id autogenerado en el objeto.
     *
     * @param presupuesto datos a insertar.
     * @return {@code true} si se inserto exactamente una fila.
     * @throws IllegalArgumentException si los datos o la categoria no son
     *                                  validos.
     * @throws SQLException              si falla la base (por ejemplo, por
     *                                   duplicar usuario/categoria/anio/mes).
     */
    public boolean insertar(Presupuesto presupuesto) throws SQLException {
        validarPresupuesto(presupuesto);

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(
                     SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            validarCategoriaPresupuestable(con, presupuesto.getIdCategoria());

            ps.setInt(1, presupuesto.getIdUsuario());
            ps.setInt(2, presupuesto.getIdCategoria());
            ps.setInt(3, presupuesto.getAnio());
            ps.setInt(4, presupuesto.getMes());
            ps.setBigDecimal(5, presupuesto.getMontoPlanificado());
            ps.setString(6, recortarObservaciones(presupuesto.getObservaciones()));

            int filas = ps.executeUpdate();

            if (filas == 1) {
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        presupuesto.setIdPresupuesto(claves.getInt(1));
                    }
                }
            }
            return filas == 1;
        }
    }

    /**
     * Actualiza un presupuesto existente.
     *
     * @param presupuesto presupuesto con id valido y datos nuevos.
     * @return {@code true} si se actualizo exactamente una fila.
     * @throws IllegalArgumentException si el presupuesto o la categoria no son
     *                                  validos.
     * @throws SQLException              si falla la base.
     */
    public boolean actualizar(Presupuesto presupuesto) throws SQLException {
        validarPresupuesto(presupuesto);

        if (presupuesto.getIdPresupuesto() <= 0) {
            throw new IllegalArgumentException("ID de presupuesto invalido.");
        }

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            validarCategoriaPresupuestable(con, presupuesto.getIdCategoria());

            ps.setInt(1, presupuesto.getIdUsuario());
            ps.setInt(2, presupuesto.getIdCategoria());
            ps.setInt(3, presupuesto.getAnio());
            ps.setInt(4, presupuesto.getMes());
            ps.setBigDecimal(5, presupuesto.getMontoPlanificado());
            ps.setString(6, recortarObservaciones(presupuesto.getObservaciones()));
            ps.setInt(7, presupuesto.getIdPresupuesto());

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Elimina un presupuesto por su identificador.
     *
     * @param id identificador del presupuesto.
     * @return {@code true} si se elimino exactamente una fila.
     * @throws IllegalArgumentException si el id no es valido.
     * @throws SQLException              si falla la base.
     */
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de presupuesto invalido: " + id);
        }

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /* =============================================================
       4) VALIDACIONES
       ============================================================= */

    /**
     * Valida los datos basicos de un presupuesto.
     *
     * @param presupuesto objeto a validar.
     * @throws IllegalArgumentException si alguna regla no se cumple.
     */
    public void validarPresupuesto(Presupuesto presupuesto) {
        if (presupuesto == null) {
            throw new IllegalArgumentException("El presupuesto no puede ser nulo.");
        }
        if (presupuesto.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El id de usuario debe ser mayor que cero.");
        }
        if (presupuesto.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("El id de categoria debe ser mayor que cero.");
        }
        validarPeriodo(presupuesto.getAnio(), presupuesto.getMes());

        if (presupuesto.getMontoPlanificado() == null) {
            throw new IllegalArgumentException("El monto planificado es obligatorio.");
        }
        if (presupuesto.getMontoPlanificado().signum() < 0) {
            throw new IllegalArgumentException("El monto planificado no puede ser negativo.");
        }
    }

    /**
     * Comprueba que la categoria exista y que no sea exclusiva de INGRESO.
     * Las categorias GASTO y AMBOS son validas para presupuestar.
     */
    private void validarCategoriaPresupuestable(Connection con, int idCategoria)
            throws SQLException {
        String sql = "SELECT tipo_permitido FROM categorias WHERE id_categoria = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("La categoria no existe.");
                }

                String permitido = rs.getString("tipo_permitido");
                if ("INGRESO".equals(permitido)) {
                    throw new IllegalArgumentException(
                            "No se puede presupuestar una categoria exclusiva de INGRESO.");
                }
            }
        }
    }

    /** Valida que el periodo tenga un anio razonable y un mes entre 1 y 12. */
    private void validarPeriodo(int anio, int mes) {
        if (anio < 1900 || anio > 2200) {
            throw new IllegalArgumentException("El anio no es valido: " + anio);
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12.");
        }
    }

    /* =============================================================
       5) AUXILIARES
       ============================================================= */

    /** Ejecuta un SELECT de dos columnas (id, texto) para los combos. */
    private List<Object[]> consultarOpciones(String sql, CargaParametros parametros)
            throws SQLException {

        List<Object[]> opciones = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    private List<Presupuesto> consultarPresupuestos(String sql, CargaParametros parametros)
            throws SQLException {
        List<Presupuesto> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (parametros != null) {
                parametros.cargar(ps);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPresupuesto(rs));
                }
            }
        }
        return lista;
    }

    private Presupuesto mapearPresupuesto(ResultSet rs) throws SQLException {
        return new Presupuesto(
                rs.getInt("id_presupuesto"),
                rs.getInt("id_usuario"),
                rs.getInt("id_categoria"),
                rs.getInt("anio"),
                rs.getInt("mes"),
                rs.getBigDecimal("monto_planificado"),
                rs.getString("observaciones"));
    }

    /** La columna admite 180 caracteres; el DAO recorta para no romper el INSERT. */
    private String recortarObservaciones(String observaciones) {
        if (observaciones == null) {
            return null;
        }
        String texto = observaciones.trim();
        if (texto.isEmpty()) {
            return null;
        }
        return texto.length() <= 180 ? texto : texto.substring(0, 180);
    }

    @FunctionalInterface
    private interface CargaParametros {
        void cargar(PreparedStatement ps) throws SQLException;
    }
}
