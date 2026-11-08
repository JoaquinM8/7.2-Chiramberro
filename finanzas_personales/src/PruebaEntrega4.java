import dao.MovimientoDAO;
import dao.PresupuestoDAO;
import modelo.ComparacionPresupuesto;
import modelo.Movimiento;
import modelo.Presupuesto;
import modelo.ResumenMensual;
import servicio.FinanzasServicio;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Prueba integradora de la Entrega 4.
 *
 * <p>Verifica las operaciones que invoca la interfaz Swing: carga de combos,
 * alta, modificacion, eliminacion y filtros de movimientos; resumen mensual;
 * alta/actualizacion de presupuestos por clave unica y comparacion contra el
 * gasto real.</p>
 *
 * <p>Usa datos temporales en octubre de 2026 y los elimina al finalizar para
 * no alterar el conjunto de datos de marzo de 2026.</p>
 */
public class PruebaEntrega4 {

    private static final LocalDate DESDE_MARZO = LocalDate.of(2026, 3, 1);
    private static final LocalDate HASTA_ABRIL = LocalDate.of(2026, 4, 1);

    private static final LocalDate FECHA_TEMPORAL = LocalDate.of(2026, 10, 15);
    private static final String DESCRIPCION_TEMPORAL = "Prueba GUI Entrega 4";

    private static int fallos = 0;

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" PRUEBA INTEGRADORA - ENTREGA 4");
        System.out.println(" Swing + DAO + JDBC + MySQL/MariaDB");
        System.out.println("=========================================================");
        System.out.println("Base    : finanzas_personales");
        System.out.println("Periodo : " + DESDE_MARZO + " a " + HASTA_ABRIL.minusDays(1));
        System.out.println();

        MovimientoDAO movimientoDAO = new MovimientoDAO();
        PresupuestoDAO presupuestoDAO = new PresupuestoDAO();
        FinanzasServicio servicio = new FinanzasServicio();

        try {
            probarCombos(movimientoDAO, presupuestoDAO);
            probarFiltros(movimientoDAO);
            probarResumen(movimientoDAO, servicio);
            probarComparacionPresupuestos(presupuestoDAO);
            probarCrudMovimiento(movimientoDAO);
            probarPresupuestoUpsert(presupuestoDAO);
            probarValidaciones(movimientoDAO, presupuestoDAO);
            probarEstadoFinal(movimientoDAO, presupuestoDAO);
        } catch (SQLException ex) {
            fallos++;
            System.err.println();
            System.err.println("ERROR SQL: " + ex.getMessage());
            System.err.println("SQLState: " + ex.getSQLState()
                    + " / codigo: " + ex.getErrorCode());
            System.err.println("Revisa que MySQL este iniciado y que existan");
            System.err.println("las tablas movimientos y presupuestos.");
        }

        System.out.println();
        System.out.println("=========================================================");
        if (fallos == 0) {
            System.out.println(" RESULTADO: TODAS LAS PRUEBAS FINALIZARON OK");
        } else {
            System.out.println(" RESULTADO: " + fallos + " PRUEBA(S) CON FALLOS");
        }
        System.out.println("=========================================================");
    }

    /* =============================================================
       1) COMBOS DE LA VISTA
       ============================================================= */

    private static void probarCombos(MovimientoDAO movimientoDAO,
                                     PresupuestoDAO presupuestoDAO)
            throws SQLException {
        seccion("1) CARGA DE COMBOS (sin SQL en la vista)");

        List<Object[]> cuentas = movimientoDAO.listarCuentasActivas();
        List<Object[]> categorias = movimientoDAO.listarCategorias();
        List<Object[]> categoriasGasto = movimientoDAO.listarCategoriasPorTipo("GASTO");
        List<Object[]> usuarios = presupuestoDAO.listarUsuarios();
        List<Object[]> categoriasPres = presupuestoDAO.listarCategoriasPresupuestables();

        System.out.println("Cuentas activas          : " + cuentas.size());
        System.out.println("Categorias totales       : " + categorias.size());
        System.out.println("Categorias para GASTO    : " + categoriasGasto.size());
        System.out.println("Usuarios para presupuesto: " + usuarios.size());
        System.out.println("Categorias presupuestables: " + categoriasPres.size());

        imprimirOpciones("Cuentas", cuentas);
        imprimirOpciones("Categorias GASTO/AMBOS", categoriasGasto);
        imprimirOpciones("Usuarios", usuarios);

        verificar(!cuentas.isEmpty(), "Hay cuentas activas para el combo.");
        verificar(!categoriasGasto.isEmpty(), "Hay categorias de gasto para el combo.");
        verificar(!usuarios.isEmpty(), "Hay usuarios para el combo de presupuestos.");
        System.out.println();
    }

    /* =============================================================
       2) FILTROS
       ============================================================= */

    private static void probarFiltros(MovimientoDAO dao) throws SQLException {
        seccion("2) FILTROS DE MOVIMIENTOS");

        List<Movimiento> gastosMarzo = dao.listarPorTipoYFechas(
                "GASTO", DESDE_MARZO, HASTA_ABRIL);
        List<Movimiento> textoLuz = dao.buscarPorDescripcion("luz");

        System.out.println("GASTO marzo 2026       : " + gastosMarzo.size() + " movimientos");
        System.out.println("Descripcion contiene luz: " + textoLuz.size() + " movimientos");

        for (Movimiento m : textoLuz) {
            System.out.println("  - " + m.getFecha() + " | "
                    + m.getDescripcion() + " | $" + m.getImporte().toPlainString());
        }

        verificar(!gastosMarzo.isEmpty(), "El filtro por tipo y fechas devuelve datos.");
        verificar(!textoLuz.isEmpty(), "El filtro por descripcion devuelve datos.");
        System.out.println();
    }

    /* =============================================================
       3) RESUMEN MENSUAL
       ============================================================= */

    private static void probarResumen(MovimientoDAO dao, FinanzasServicio servicio)
            throws SQLException {
        seccion("3) RESUMEN MENSUAL Y TASA DE AHORRO");

        ResumenMensual resumen = dao.resumenMensual(DESDE_MARZO, HASTA_ABRIL);

        System.out.println("Ingresos : $" + resumen.getIngresos().toPlainString());
        System.out.println("Gastos   : $" + resumen.getGastos().toPlainString());
        System.out.println("Resultado: $" + resumen.getResultado().toPlainString());
        System.out.println("Tasa de ahorro: "
                + servicio.calcularTasaAhorro(resumen).toPlainString() + " %");

        verificar(resumen.getIngresos().signum() > 0,
                "El periodo de ejemplo tiene ingresos.");
        verificar(resumen.getGastos().signum() > 0,
                "El periodo de ejemplo tiene gastos.");
        System.out.println();
    }

    /* =============================================================
       4) COMPARACION DE PRESUPUESTOS
       ============================================================= */

    private static void probarComparacionPresupuestos(PresupuestoDAO dao)
            throws SQLException {
        seccion("4) PRESUPUESTO PLANIFICADO VS GASTO REAL (Ana, marzo 2026)");

        List<ComparacionPresupuesto> filas = dao.compararPresupuestos(1, 2026, 3);

        System.out.printf("%-18s %14s %14s %14s %10s  %s%n",
                "CATEGORIA", "PLANIFICADO", "GASTO REAL", "DISPONIBLE",
                "EJECUCION", "ESTADO");
        System.out.println("--------------------------------------------------------------------------------");

        for (ComparacionPresupuesto c : filas) {
            System.out.printf("%-18s %14s %14s %14s %9s%%  %s%n",
                    c.getCategoria(),
                    c.getMontoPlanificado().toPlainString(),
                    c.getGastoReal().toPlainString(),
                    c.getDisponible().toPlainString(),
                    c.getPorcentajeEjecutado().toPlainString(),
                    c.getEstado());
        }

        verificar(!filas.isEmpty(), "Ana tiene presupuestos cargados para marzo 2026.");
        verificar(filas.stream().anyMatch(c -> "EXCEDIDO".equals(c.getEstado())),
                "Se observa al menos una categoria excedida.");
        verificar(filas.stream().anyMatch(c -> "DENTRO DEL PRESUPUESTO".equals(c.getEstado())),
                "Se observa al menos una categoria dentro del presupuesto.");
        System.out.println();
    }

    /* =============================================================
       5) CRUD TEMPORAL DE MOVIMIENTOS
       ============================================================= */

    private static void probarCrudMovimiento(MovimientoDAO dao) throws SQLException {
        seccion("5) CRUD TEMPORAL DE MOVIMIENTOS");

        Movimiento temporal = new Movimiento(
                2, 2, "INGRESO", FECHA_TEMPORAL,
                new BigDecimal("12345.67"), DESCRIPCION_TEMPORAL,
                "Transferencia", false);

        int id = 0;

        try {
            boolean insertado = dao.insertar(temporal);
            id = temporal.getIdMovimiento();

            verificar(insertado && id > 0, "Alta: movimiento insertado con id " + id);
            System.out.println("Alta      : id=" + id
                    + " / importe=$" + temporal.getImporte().toPlainString());

            Movimiento recuperado = dao.buscarPorId(id);
            verificar(recuperado != null, "Lectura: el movimiento se recupera por id.");

            recuperado.setImporte(new BigDecimal("23456.78"));
            recuperado.setDescripcion(DESCRIPCION_TEMPORAL + " modificada");

            boolean actualizado = dao.actualizar(recuperado);
            Movimiento modificado = dao.buscarPorId(id);

            verificar(actualizado && modificado != null
                            && modificado.getImporte().compareTo(
                                    new BigDecimal("23456.78")) == 0,
                    "Modificacion: el importe actualizado persiste.");
            System.out.println("Modificacion: importe=$"
                    + (modificado == null ? "-" : modificado.getImporte().toPlainString()));

            final int idTemporal = id;
            List<Movimiento> filtrados = dao.listarPorTipoYFechas(
                    "INGRESO", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1));
            boolean aparece = filtrados.stream()
                    .anyMatch(m -> m.getIdMovimiento() == idTemporal);

            verificar(aparece, "Filtro por tipo y fecha incluye el movimiento temporal.");

            List<Movimiento> porTexto = dao.buscarPorDescripcion("GUI Entrega 4");
            boolean apareceTexto = porTexto.stream()
                    .anyMatch(m -> m.getIdMovimiento() == idTemporal);

            verificar(apareceTexto, "Filtro por descripcion incluye el movimiento temporal.");

            boolean eliminado = dao.eliminar(id);
            boolean sigueExistiendo = dao.buscarPorId(id) != null;

            verificar(eliminado && !sigueExistiendo,
                    "Eliminacion: el movimiento temporal ya no existe.");
            System.out.println("Eliminacion: id=" + id + " eliminado.");

        } finally {
            // Limpieza defensiva si una verificacion anterior fallo.
            if (id > 0 && dao.buscarPorId(id) != null) {
                dao.eliminar(id);
                System.out.println("Limpieza  : se elimino el movimiento temporal id=" + id);
            }
        }
        System.out.println();
    }

    /* =============================================================
       6) PRESUPUESTO: UPSERT Y COMPARACION
       ============================================================= */

    private static void probarPresupuestoUpsert(PresupuestoDAO dao)
            throws SQLException {
        seccion("6) GUARDAR/ACTUALIZAR PRESUPUESTO (clave unica)");

        int idUsuario = 1;
        int idCategoria = 9;
        int anio = 2026;
        int mes = 10;

        eliminarPresupuestoSiExiste(dao, idUsuario, idCategoria, anio, mes);

        boolean primero = dao.guardar(idUsuario, idCategoria, anio, mes,
                new BigDecimal("100000.00"), "Prueba upsert Entrega 4");
        Presupuesto despuesPrimero = buscarPresupuesto(
                dao, idUsuario, idCategoria, anio, mes);

        verificar(primero && despuesPrimero != null,
                "Alta de presupuesto temporal.");
        System.out.println("Alta: id=" + (despuesPrimero == null
                ? "-" : despuesPrimero.getIdPresupuesto())
                + " / monto=$" + (despuesPrimero == null
                ? "-" : despuesPrimero.getMontoPlanificado().toPlainString()));

        boolean segundo = dao.guardar(idUsuario, idCategoria, anio, mes,
                new BigDecimal("120000.00"), "Prueba upsert actualizada");
        Presupuesto despuesSegundo = buscarPresupuesto(
                dao, idUsuario, idCategoria, anio, mes);

        verificar(segundo && despuesSegundo != null
                        && despuesSegundo.getMontoPlanificado().compareTo(
                                new BigDecimal("120000.00")) == 0,
                "Actualizacion por clave unica: el monto cambio sin duplicar.");

        List<Presupuesto> delPeriodo = dao.listarPorUsuarioYPeriodo(
                idUsuario, anio, mes);
        long cantidadCategoria = delPeriodo.stream()
                .filter(p -> p.getIdCategoria() == idCategoria)
                .count();

        verificar(cantidadCategoria == 1,
                "Existe un solo presupuesto para usuario/categoria/periodo.");

        List<Object[]> comparacion = dao.comparar(idUsuario, anio, mes);
        System.out.println("Filas de comparacion para octubre 2026: " + comparacion.size());
        for (Object[] fila : comparacion) {
            System.out.println("  " + fila[1] + " | plan=$" + fila[2]
                    + " | real=$" + fila[3] + " | disponible=$" + fila[4]
                    + " | " + fila[6]);
        }

        eliminarPresupuestoSiExiste(dao, idUsuario, idCategoria, anio, mes);
        verificar(buscarPresupuesto(dao, idUsuario, idCategoria, anio, mes) == null,
                "Limpieza: se elimino el presupuesto temporal.");
        System.out.println();
    }

    /* =============================================================
       7) VALIDACIONES
       ============================================================= */

    private static void probarValidaciones(MovimientoDAO movimientoDAO,
                                           PresupuestoDAO presupuestoDAO)
            throws SQLException {
        seccion("7) VALIDACIONES");

        try {
            movimientoDAO.listarCategoriasPorTipo("OTRO");
            verificar(false, "Un tipo invalido debe ser rechazado.");
        } catch (IllegalArgumentException ex) {
            verificar(true, "Tipo invalido rechazado: " + ex.getMessage());
        }

        try {
            presupuestoDAO.guardar(1, 1, 2026, 10,
                    new BigDecimal("1000.00"), "Categoria de ingreso");
            verificar(false, "Una categoria exclusiva de INGRESO no debe presupuestarse.");
        } catch (IllegalArgumentException ex) {
            verificar(true, "Categoria de ingreso rechazada: " + ex.getMessage());
        }

        try {
            presupuestoDAO.guardar(1, 9, 2026, 13,
                    new BigDecimal("1000.00"), "Mes invalido");
            verificar(false, "Un mes fuera de rango debe ser rechazado.");
        } catch (IllegalArgumentException ex) {
            verificar(true, "Mes invalido rechazado: " + ex.getMessage());
        }

        System.out.println();
    }

    /* =============================================================
       8) ESTADO FINAL
       ============================================================= */

    private static void probarEstadoFinal(MovimientoDAO movimientoDAO,
                                          PresupuestoDAO presupuestoDAO)
            throws SQLException {
        seccion("8) ESTADO FINAL");

        System.out.println("Movimientos : " + movimientoDAO.listarTodos().size());
        System.out.println("Presupuestos: " + presupuestoDAO.listarTodos().size());

        verificar(movimientoDAO.listarTodos().stream()
                        .noneMatch(m -> DESCRIPCION_TEMPORAL.equals(m.getDescripcion())),
                "No quedaron movimientos temporales de la prueba.");

        verificar(presupuestoDAO.listarTodos().stream()
                        .noneMatch(p -> p.getIdCategoria() == 9
                                && p.getAnio() == 2026 && p.getMes() == 10),
                "No quedo el presupuesto temporal de la prueba.");
        System.out.println();
    }

    /* =============================================================
       9) AUXILIARES
       ============================================================= */

    private static void eliminarPresupuestoSiExiste(PresupuestoDAO dao,
                                                     int idUsuario,
                                                     int idCategoria,
                                                     int anio,
                                                     int mes)
            throws SQLException {
        Presupuesto existente = buscarPresupuesto(
                dao, idUsuario, idCategoria, anio, mes);
        if (existente != null) {
            dao.eliminar(existente.getIdPresupuesto());
        }
    }

    private static Presupuesto buscarPresupuesto(PresupuestoDAO dao,
                                                  int idUsuario,
                                                  int idCategoria,
                                                  int anio,
                                                  int mes)
            throws SQLException {
        for (Presupuesto p : dao.listarPorUsuarioYPeriodo(idUsuario, anio, mes)) {
            if (p.getIdCategoria() == idCategoria) {
                return p;
            }
        }
        return null;
    }

    private static void imprimirOpciones(String titulo, List<Object[]> opciones) {
        System.out.print(titulo + ": ");
        for (int i = 0; i < opciones.size(); i++) {
            Object[] fila = opciones.get(i);
            if (i > 0) {
                System.out.print(" | ");
            }
            System.out.print(fila[0] + "=" + fila[1]);
        }
        System.out.println();
    }

    private static void seccion(String titulo) {
        System.out.println("---------------------------------------------------------");
        System.out.println(" " + titulo);
        System.out.println("---------------------------------------------------------");
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (condicion) {
            System.out.println("  [OK] " + descripcion);
        } else {
            fallos++;
            System.out.println("  [FALLO] " + descripcion);
        }
    }
}
