import dao.MovimientoDAO;
import dao.PresupuestoDAO;
import modelo.ComparacionPresupuesto;
import modelo.GastoCategoria;
import modelo.Movimiento;
import modelo.Presupuesto;
import modelo.ResumenDiario;
import modelo.ResumenMensual;
import servicio.FinanzasServicio;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Prueba integradora de la Entrega 3.
 *
 * <p>Ejercita el CRUD completo de movimientos, las validaciones, las
 * consultas de analisis financiero, el servicio de calculo y el CRUD de
 * presupuestos. Esta pensada para ejecutarse desde consola y usar su salida
 * como evidencia de la entrega.</p>
 *
 * <p>Uso:</p>
 * <pre>
 *   java -cp "bin;lib/mysql-connector-j-26.7.0.jar" PruebaEntrega3
 * </pre>
 */
public class PruebaEntrega3 {

    private static final LocalDate DESDE = LocalDate.of(2026, 3, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 4, 1);

    private static final String DESCRIPCION_TEMPORAL = "CRUD temporal Entrega 3";

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" PRUEBA INTEGRADORA - ENTREGA 3");
        System.out.println(" CRUD, consultas SQL y logica financiera");
        System.out.println("=========================================================");
        System.out.println("Base    : finanzas_personales");
        System.out.println("Periodo : " + DESDE + " a " + HASTA.minusDays(1));
        System.out.println();

        MovimientoDAO movimientoDAO = new MovimientoDAO();
        PresupuestoDAO presupuestoDAO = new PresupuestoDAO();
        FinanzasServicio servicio = new FinanzasServicio();

        try {
            probarResumenMensual(movimientoDAO, servicio);
            probarGastosPorCategoria(movimientoDAO);
            probarIngresosGastosPorDia(movimientoDAO);
            probarGastoPromedio(movimientoDAO);
            probarPresupuestos(presupuestoDAO, servicio);
            probarCrudMovimientos(movimientoDAO);
            probarValidaciones(movimientoDAO);
            probarCrudPresupuestos(presupuestoDAO);
        } catch (SQLException e) {
            System.err.println();
            System.err.println("ERROR SQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState()
                    + " / codigo: " + e.getErrorCode());
            System.err.println("Revisa que MySQL este iniciado y que existan");
            System.err.println("las tablas movimientos y presupuestos.");
        }
    }

    /* =============================================================
       1) RESUMEN MENSUAL Y SERVICIO FINANCIERO
       ============================================================= */
    private static void probarResumenMensual(MovimientoDAO dao, FinanzasServicio servicio)
            throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 1) RESUMEN MENSUAL (MySQL: SUM + CASE)");
        System.out.println("---------------------------------------------------------");

        ResumenMensual resumen = dao.resumenMensual(DESDE, HASTA);

        System.out.printf("  Ingresos : %14s%n", resumen.getIngresos().toPlainString());
        System.out.printf("  Gastos   : %14s%n", resumen.getGastos().toPlainString());
        System.out.printf("  Resultado: %14s%n", resumen.getResultado().toPlainString());

        if (resumen.getIngresos().signum() > 0) {
            System.out.printf("  Tasa de ahorro: %s %%%n",
                    servicio.calcularTasaAhorro(resumen));
            System.out.printf("  Gasto sobre ingresos: %s %%%n",
                    servicio.calcularPorcentajeGastoSobreIngresos(resumen));
        } else {
            System.out.println("  Tasa de ahorro: no calculable (sin ingresos).");
        }

        // Caso de prueba: un mes sin movimientos debe devolver ceros.
        ResumenMensual vacio = dao.resumenMensual(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1));
        System.out.println();
        System.out.println("  Mes sin movimientos (mayo 2026):");
        System.out.println("    Ingresos=" + vacio.getIngresos().toPlainString()
                + " / Gastos=" + vacio.getGastos().toPlainString()
                + " / Resultado=" + vacio.getResultado().toPlainString()
                + " / estaVacio=" + vacio.estaVacio());
        System.out.println();
    }

    /* =============================================================
       2) GASTOS POR CATEGORIA Y PORCENTAJES
       ============================================================= */
    private static void probarGastosPorCategoria(MovimientoDAO dao) throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 2) GASTOS POR CATEGORIA (JOIN + GROUP BY + SUM)");
        System.out.println("---------------------------------------------------------");

        List<GastoCategoria> gastos = dao.porcentajeGastosPorCategoria(DESDE, HASTA);

        System.out.printf("%-4s %-22s %14s %6s %10s%n",
                "ID", "CATEGORIA", "TOTAL GASTADO", "CANT.", "% GASTO");
        System.out.println("-------------------------------------------------------------------");

        for (GastoCategoria gasto : gastos) {
            System.out.printf("%-4d %-22s %14s %6d %9s %%%n",
                    gasto.getIdCategoria(),
                    gasto.getNombreCategoria(),
                    gasto.getTotalGastado().toPlainString(),
                    gasto.getCantidadMovimientos(),
                    gasto.getPorcentaje() == null
                            ? "-" : gasto.getPorcentaje().toPlainString());
        }
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Categorias con gasto: " + gastos.size());
        System.out.println();
    }

    /* =============================================================
       3) INGRESOS Y GASTOS POR DIA
       ============================================================= */
    private static void probarIngresosGastosPorDia(MovimientoDAO dao) throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 3) INGRESOS Y GASTOS POR DIA (GROUP BY fecha)");
        System.out.println("---------------------------------------------------------");

        List<ResumenDiario> dias = dao.ingresosGastosPorDia(DESDE, HASTA);

        System.out.printf("%-12s %14s %14s %14s%n",
                "FECHA", "INGRESOS", "GASTOS", "RESULTADO");
        System.out.println("---------------------------------------------------------");

        for (ResumenDiario dia : dias) {
            System.out.printf("%-12s %14s %14s %14s%n",
                    dia.getFecha(),
                    dia.getIngresos().toPlainString(),
                    dia.getGastos().toPlainString(),
                    dia.getResultado().toPlainString());
        }
        System.out.println("---------------------------------------------------------");
        System.out.println("Dias con movimientos: " + dias.size());
        System.out.println();
    }

    /* =============================================================
       4) GASTO PROMEDIO POR MOVIMIENTO
       ============================================================= */
    private static void probarGastoPromedio(MovimientoDAO dao) throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 4) GASTO PROMEDIO POR MOVIMIENTO");
        System.out.println("---------------------------------------------------------");
        System.out.println("  Promedio: "
                + dao.gastoPromedioPorMovimiento(DESDE, HASTA).toPlainString());
        System.out.println();
    }

    /* =============================================================
       5) PRESUPUESTO VS. GASTO REAL
       ============================================================= */
    private static void probarPresupuestos(PresupuestoDAO dao, FinanzasServicio servicio)
            throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 5) PRESUPUESTO VS. GASTO REAL");
        System.out.println("---------------------------------------------------------");

        List<Presupuesto> cargados = dao.listarPorUsuarioYPeriodo(1, 2026, 3);
        System.out.println("Presupuestos cargados para Ana Gomez en marzo 2026: "
                + cargados.size());

        List<ComparacionPresupuesto> comparaciones =
                dao.compararPresupuestos(1, 2026, 3);

        System.out.println();
        System.out.printf("%-22s %12s %12s %12s %8s %-22s%n",
                "CATEGORIA", "PLANIFICADO", "GASTO REAL", "DISPONIBLE",
                "EJEC.%", "ESTADO");
        System.out.println("--------------------------------------------------------------------------------"
                + "----------");

        for (ComparacionPresupuesto c : comparaciones) {
            System.out.printf("%-22s %12s %12s %12s %7s%% %-22s%n",
                    c.getCategoria(),
                    c.getMontoPlanificado().toPlainString(),
                    c.getGastoReal().toPlainString(),
                    c.getDisponible().toPlainString(),
                    c.getPorcentajeEjecutado().toPlainString(),
                    c.getEstado());
        }
        System.out.println("--------------------------------------------------------------------------------"
                + "----------");
        System.out.println();
    }

    /* =============================================================
       6) CRUD DE MOVIMIENTOS
       ============================================================= */
    private static void probarCrudMovimientos(MovimientoDAO dao) throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 6) CRUD DE MOVIMIENTOS");
        System.out.println("---------------------------------------------------------");

        limpiarMovimientosTemporales(dao);

        Movimiento movimiento = new Movimiento(
                2,                                  // Cuenta bancaria Ana
                2,                                  // Freelance (INGRESO)
                "INGRESO",
                LocalDate.of(2026, 3, 15),
                new BigDecimal("12345.67"),
                DESCRIPCION_TEMPORAL,
                "Transferencia",
                false);

        boolean insertado = false;
        try {
            System.out.println("6.1 Insertar movimiento temporal...");
            insertado = dao.insertar(movimiento);
            System.out.println("    Resultado: " + insertado
                    + " / id generado: " + movimiento.getIdMovimiento());

            if (!insertado) {
                System.out.println("    No se puede continuar con el CRUD.");
                return;
            }

            System.out.println();
            System.out.println("6.2 Buscar por ID...");
            Movimiento encontrado = dao.buscarPorId(movimiento.getIdMovimiento());
            System.out.println("    " + encontrado);

            System.out.println();
            System.out.println("6.3 Actualizar importe, descripcion y recurrencia...");
            encontrado.setImporte(new BigDecimal("15000.00"));
            encontrado.setDescripcion(DESCRIPCION_TEMPORAL + " (actualizado)");
            encontrado.setRecurrente(true);
            boolean actualizado = dao.actualizar(encontrado);
            System.out.println("    Resultado: " + actualizado);

            Movimiento releido = dao.buscarPorId(encontrado.getIdMovimiento());
            System.out.println("    Releido: " + releido);

            System.out.println();
            System.out.println("6.4 Listar INGRESOS de marzo 2026...");
            List<Movimiento> ingresos = dao.listarPorTipoYFechas("INGRESO", DESDE, HASTA);
            System.out.println("    Cantidad: " + ingresos.size());
            PruebaJDBC.imprimirTabla(ingresos);

            System.out.println();
            System.out.println("6.5 Buscar por texto en la descripcion...");
            List<Movimiento> coincidencias =
                    dao.buscarPorDescripcion(DESCRIPCION_TEMPORAL);
            System.out.println("    Coincidencias: " + coincidencias.size());
            PruebaJDBC.imprimirTabla(coincidencias);

            System.out.println();
            System.out.println("6.6 Eliminar el movimiento temporal...");
            boolean eliminado = dao.eliminar(encontrado.getIdMovimiento());
            insertado = false;
            System.out.println("    Resultado: " + eliminado);

            System.out.println();
            System.out.println("6.7 Verificar que ya no existe...");
            Movimiento borrado = dao.buscarPorId(encontrado.getIdMovimiento());
            System.out.println("    buscarPorId devuelve: " + borrado);
        } finally {
            // Limpieza defensiva si una prueba anterior fallo a mitad de camino.
            if (insertado && movimiento.getIdMovimiento() > 0) {
                dao.eliminar(movimiento.getIdMovimiento());
            }
        }

        probarInsercionGasto(dao);
        System.out.println();
    }

    /**
     * Inserta un GASTO temporal con una categoria de gasto valida, lo relee y
     * lo elimina. Complementa el caso de insercion de INGRESO y deja evidencia
     * de que el CRUD funciona para ambos tipos.
     */
    private static void probarInsercionGasto(MovimientoDAO dao) throws SQLException {
        System.out.println();
        System.out.println("6.8 Insertar, releer y eliminar un GASTO temporal...");

        Movimiento gasto = new Movimiento(
                1,                                  // Efectivo Ana
                4,                                  // Alimentacion (GASTO)
                "GASTO",
                LocalDate.of(2026, 3, 16),
                new BigDecimal("5432.10"),
                DESCRIPCION_TEMPORAL + " - GASTO",
                "Efectivo",
                false);

        boolean insertado = false;
        try {
            insertado = dao.insertar(gasto);
            System.out.println("    Resultado: " + insertado
                    + " / id generado: " + gasto.getIdMovimiento());

            Movimiento releido = dao.buscarPorId(gasto.getIdMovimiento());
            System.out.println("    Releido: " + releido);
        } finally {
            if (insertado && gasto.getIdMovimiento() > 0) {
                boolean eliminado = dao.eliminar(gasto.getIdMovimiento());
                System.out.println("    Eliminado: " + eliminado);
            }
        }
    }

    /** Elimina movimientos temporales que hayan quedado de una ejecucion previa. */
    private static void limpiarMovimientosTemporales(MovimientoDAO dao) throws SQLException {
        List<Movimiento> restos = dao.buscarPorDescripcion(DESCRIPCION_TEMPORAL);
        for (Movimiento resto : restos) {
            dao.eliminar(resto.getIdMovimiento());
        }
    }

    /* =============================================================
       7) VALIDACIONES Y CASOS DE PRUEBA
       ============================================================= */
    private static void probarValidaciones(MovimientoDAO dao) throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 7) VALIDACIONES Y CASOS DE PRUEBA");
        System.out.println("---------------------------------------------------------");

        probarIllegalArgument("importe igual a cero", () ->
                dao.insertar(new Movimiento(1, 4, "GASTO", DESDE, BigDecimal.ZERO,
                        "Importe cero", "Efectivo", false)));

        probarIllegalArgument("tipo incorrecto 'COMPRA'", () ->
                dao.insertar(new Movimiento(1, 4, "COMPRA", DESDE,
                        new BigDecimal("1000.00"), "Tipo incorrecto", "Efectivo", false)));

        probarIllegalArgument("categoria inexistente (id 999)", () ->
                dao.insertar(new Movimiento(1, 999, "GASTO", DESDE,
                        new BigDecimal("1000.00"), "Categoria inexistente",
                        "Efectivo", false)));

        probarIllegalArgument("categoria incompatible (Sueldo con GASTO)", () ->
                dao.insertar(new Movimiento(1, 1, "GASTO", DESDE,
                        new BigDecimal("1000.00"), "Categoria incompatible",
                        "Efectivo", false)));

        probarIllegalArgument("rango invertido en resumenMensual", () ->
                dao.resumenMensual(HASTA, DESDE));

        probarIllegalArgument("tipo invalido en listarPorTipoYFechas", () ->
                dao.listarPorTipoYFechas("TRANSFERENCIA", DESDE, HASTA));

        // Actualizar un ID que no existe: no es una excepcion, devuelve false.
        Movimiento inexistente = new Movimiento(
                2, 2, "INGRESO", DESDE, new BigDecimal("1000.00"),
                "Actualizacion de ID inexistente", "Transferencia", false);
        inexistente.setIdMovimiento(999999);
        boolean actualizado = dao.actualizar(inexistente);
        System.out.println("[OK] actualizar ID inexistente -> devuelve " + actualizado);

        // Eliminar un ID que no existe: devuelve false.
        boolean eliminado = dao.eliminar(999999);
        System.out.println("[OK] eliminar ID inexistente -> devuelve " + eliminado);

        System.out.println();
    }

    /** Ejecuta una accion que debe fallar con IllegalArgumentException. */
    private static void probarIllegalArgument(String caso, AccionSQL accion) {
        try {
            accion.ejecutar();
            System.err.println("[FALLO] " + caso + ": no se rechazo.");
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] " + caso + " -> " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] " + caso + " -> " + e.getMessage());
        }
    }

    /* =============================================================
       8) CRUD Y VALIDACIONES DE PRESUPUESTOS
       ============================================================= */
    private static void probarCrudPresupuestos(PresupuestoDAO dao) throws SQLException {
        System.out.println("---------------------------------------------------------");
        System.out.println(" 8) CRUD Y VALIDACIONES DE PRESUPUESTOS");
        System.out.println("---------------------------------------------------------");

        // Limpieza de restos de ejecuciones anteriores. Se usa la categoria
        // 9 (Educacion) para no tocar el presupuesto de junio de la categoria
        // 8 que carga el script 06_presupuestos.sql.
        limpiarPresupuestosTemporales(dao, 2026, 8, 9);
        limpiarPresupuestosTemporales(dao, 2026, 9, 9);

        Presupuesto presupuesto = new Presupuesto(
                1, 9, 2026, 8, new BigDecimal("50000.00"),
                "Presupuesto temporal Entrega 3");

        boolean insertado = false;
        try {
            System.out.println("8.1 Insertar presupuesto de Educacion para agosto 2026...");
            insertado = dao.insertar(presupuesto);
            System.out.println("    Resultado: " + insertado
                    + " / id generado: " + presupuesto.getIdPresupuesto());

            System.out.println();
            System.out.println("8.2 Buscar por ID...");
            System.out.println("    " + dao.buscarPorId(presupuesto.getIdPresupuesto()));

            System.out.println();
            System.out.println("8.3 Comparar presupuesto sin gastos...");
            imprimirComparaciones(dao.compararPresupuestos(1, 2026, 8));

            System.out.println();
            System.out.println("8.4 Actualizar monto planificado...");
            presupuesto.setMontoPlanificado(new BigDecimal("60000.00"));
            presupuesto.setObservaciones("Presupuesto temporal actualizado");
            boolean actualizado = dao.actualizar(presupuesto);
            System.out.println("    Resultado: " + actualizado);
            System.out.println("    " + dao.buscarPorId(presupuesto.getIdPresupuesto()));

            System.out.println();
            System.out.println("8.5 Eliminar presupuesto temporal...");
            boolean eliminado = dao.eliminar(presupuesto.getIdPresupuesto());
            insertado = false;
            System.out.println("    Resultado: " + eliminado);
            System.out.println("    buscarPorId devuelve: "
                    + dao.buscarPorId(presupuesto.getIdPresupuesto()));
        } finally {
            if (insertado && presupuesto.getIdPresupuesto() > 0) {
                dao.eliminar(presupuesto.getIdPresupuesto());
            }
        }

        // Regla UNIQUE: no se puede repetir usuario/categoria/anio/mes.
        Presupuesto duplicado = new Presupuesto(
                1, 9, 2026, 9, new BigDecimal("10000.00"), "Duplicado 1");
        boolean duplicadoInsertado = false;
        try {
            duplicadoInsertado = dao.insertar(duplicado);
            try {
                dao.insertar(new Presupuesto(
                        1, 9, 2026, 9, new BigDecimal("20000.00"), "Duplicado 2"));
                System.err.println("[FALLO] Se acepto un presupuesto duplicado.");
            } catch (SQLException e) {
                System.out.println("[OK] presupuesto duplicado rechazado por UNIQUE"
                        + " (codigo " + e.getErrorCode() + ").");
            }
        } finally {
            if (duplicadoInsertado && duplicado.getIdPresupuesto() > 0) {
                dao.eliminar(duplicado.getIdPresupuesto());
            }
        }

        // Una categoria exclusiva de INGRESO no se puede presupuestar.
        probarIllegalArgument("presupuesto en categoria de INGRESO", () ->
                dao.insertar(new Presupuesto(
                        1, 1, 2026, 8, new BigDecimal("1000.00"),
                        "Categoria solo de ingreso")));

        // Mes fuera de rango.
        probarIllegalArgument("mes 13 en presupuesto", () ->
                dao.insertar(new Presupuesto(
                        1, 9, 2026, 13, new BigDecimal("1000.00"),
                        "Mes invalido")));

        System.out.println();
    }

    /** Imprime comparaciones en formato de tabla. */
    private static void imprimirComparaciones(List<ComparacionPresupuesto> comparaciones) {
        if (comparaciones.isEmpty()) {
            System.out.println("    (sin presupuestos para el filtro indicado)");
            return;
        }

        System.out.printf("    %-22s %12s %12s %12s %-22s%n",
                "CATEGORIA", "PLANIFICADO", "GASTO REAL", "DISPONIBLE", "ESTADO");
        System.out.println("    ----------------------------------------------------------------"
                + "------------------");

        for (ComparacionPresupuesto c : comparaciones) {
            System.out.printf("    %-22s %12s %12s %12s %-22s%n",
                    c.getCategoria(),
                    c.getMontoPlanificado().toPlainString(),
                    c.getGastoReal().toPlainString(),
                    c.getDisponible().toPlainString(),
                    c.getEstado());
        }
    }

    /** Elimina presupuestos temporales de una categoria en el periodo indicado. */
    private static void limpiarPresupuestosTemporales(PresupuestoDAO dao, int anio, int mes,
                                                      int idCategoria)
            throws SQLException {
        List<Presupuesto> restos = dao.listarPorUsuarioYPeriodo(1, anio, mes);
        for (Presupuesto resto : restos) {
            if (resto.getIdCategoria() == idCategoria) {
                dao.eliminar(resto.getIdPresupuesto());
            }
        }
    }

    /** Accion que puede lanzar excepciones, usada por las pruebas de validacion. */
    @FunctionalInterface
    private interface AccionSQL {
        void ejecutar() throws Exception;
    }
}
