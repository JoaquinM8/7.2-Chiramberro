import dao.MovimientoDAO;
import modelo.Movimiento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Prueba 2: inserta un ingreso y un gasto ficticios, verifica la relectura
 * desde MySQL y comprueba que la validacion previa rechace los datos
 * incorrectos antes de llegar a la base.
 *
 * Tambien prueba una cuenta inexistente para ver el error de clave foranea.
 *
 * Uso:
 *   java -cp "bin;lib/mysql-connector-j-*.jar" PruebaInsercion
 */
public class PruebaInsercion {

    private static final String DESCRIPCION_INGRESO =
            "Ingreso ficticio Entrega 2 - honorarios freelance";
    private static final String DESCRIPCION_GASTO =
            "Gasto ficticio Entrega 2 - compra de prueba";

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" PRUEBA DE INSERCION Y VALIDACION - movimientos");
        System.out.println("=========================================================");

        MovimientoDAO dao = new MovimientoDAO();

        // ---- 1) Ingreso ficticio (categoria 2 = Freelance) --------------
        Movimiento ingreso = new Movimiento(
                2,                                 // id_cuenta (Cuenta bancaria Ana)
                2,                                 // id_categoria (Freelance, INGRESO)
                "INGRESO",
                LocalDate.now(),
                new BigDecimal("250000.50"),
                DESCRIPCION_INGRESO,
                "Transferencia",
                false);

        boolean ingresoOk = insertarYVerificar(dao, ingreso, "INGRESO ficticio");

        // ---- 2) Gasto ficticio (categoria 4 = Alimentacion) -------------
        Movimiento gasto = new Movimiento(
                1,                                 // id_cuenta (Efectivo Ana)
                4,                                 // id_categoria (Alimentacion, GASTO)
                "GASTO",
                LocalDate.now(),
                new BigDecimal("12345.67"),
                DESCRIPCION_GASTO,
                "Efectivo",
                false);

        boolean gastoOk = insertarYVerificar(dao, gasto, "GASTO ficticio");

        // ---- 3) Relectura y totales -------------------------------------
        mostrarEstadoActual(dao);

        if (ingresoOk && gastoOk) {
            System.out.println();
            System.out.println("Verificacion en phpMyAdmin (pestana SQL):");
            System.out.println("  SELECT id_movimiento, id_cuenta, id_categoria, tipo, fecha,");
            System.out.println("         importe, descripcion, medio_pago");
            System.out.println("  FROM movimientos");
            System.out.println("  WHERE descripcion LIKE '%ficticio Entrega 2%'");
            System.out.println("  ORDER BY id_movimiento DESC;");
        }

        // ---- 4) Casos invalidos -----------------------------------------
        System.out.println();
        System.out.println("---------------------------------------------------------");
        System.out.println(" VALIDACIONES PREVIAS (no deben llegar a MySQL)");
        System.out.println("---------------------------------------------------------");

        probarValidacion(dao,
                new Movimiento(1, 4, "GASTO", LocalDate.now(),
                        new BigDecimal("0.00"), "Importe en cero", "Efectivo", false),
                "importe igual a cero");

        probarValidacion(dao,
                new Movimiento(1, 4, "TRANSFERENCIA", LocalDate.now(),
                        new BigDecimal("1000.00"), "Tipo inexistente", "Efectivo", false),
                "tipo distinto de INGRESO/GASTO");

        probarValidacion(dao,
                new Movimiento(1, 4, "GASTO", LocalDate.now(),
                        new BigDecimal("1000.00"), "   ", "Efectivo", false),
                "descripcion vacia");

        probarValidacion(dao,
                new Movimiento(0, 4, "GASTO", LocalDate.now(),
                        new BigDecimal("1000.00"), "Sin cuenta", "Efectivo", false),
                "idCuenta invalido");

        // ---- 5) Cuenta inexistente: la FK debe rechazar el INSERT -------
        probarCuentaInexistente(dao);
    }

    /**
     * Inserta un movimiento y muestra el id autogenerado.
     *
     * @return {@code true} si la insercion fue exitosa.
     */
    private static boolean insertarYVerificar(MovimientoDAO dao, Movimiento movimiento,
                                              String caso) {
        System.out.println();
        System.out.println("--- " + caso + " ---");
        System.out.println("Insertando: " + movimiento);

        try {
            boolean insertado = dao.insertar(movimiento);

            if (insertado) {
                System.out.println("RESULTADO: insercion exitosa. ID autogenerado: "
                        + movimiento.getIdMovimiento());
                return true;
            }
            System.out.println("[FALLO] la base no inserto ninguna fila.");
        } catch (SQLException e) {
            System.err.println("[ERROR SQL] " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState()
                    + " / codigo: " + e.getErrorCode());
            System.err.println("  Revisa que existan la cuenta y la categoria indicadas.");
        }
        return false;
    }

    /** Vuelve a leer la tabla y muestra los ultimos registros y los totales. */
    private static void mostrarEstadoActual(MovimientoDAO dao) {
        System.out.println();
        System.out.println("---------------------------------------------------------");
        System.out.println(" RELECTURA DESDE LA BASE");
        System.out.println("---------------------------------------------------------");

        try {
            List<Movimiento> todos = dao.listarTodos();
            System.out.println("Total de movimientos ahora: " + todos.size());

            int hasta = Math.min(6, todos.size());
            System.out.println("Ultimos " + hasta + " movimientos (fecha descendente):");
            PruebaJDBC.imprimirTabla(todos.subList(0, hasta));

            System.out.println();
            PruebaJDBC.imprimirTotales(dao.obtenerTotales());
        } catch (SQLException e) {
            System.err.println("ERROR AL RELEER: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }

    /**
     * Prueba 24: intenta insertar un movimiento con idCuenta=99, que no existe.
     * MySQL debe rechazarlo con ERROR 1452 (violacion de clave foranea).
     */
    private static void probarCuentaInexistente(MovimientoDAO dao) {
        System.out.println();
        System.out.println("---------------------------------------------------------");
        System.out.println(" CLAVE FORANEA: CUENTA INEXISTENTE");
        System.out.println("---------------------------------------------------------");

        Movimiento invalido = new Movimiento(
                99,                                // id_cuenta que NO existe
                4,                                 // id_categoria (Alimentacion)
                "GASTO",
                LocalDate.now(),
                new BigDecimal("15000.00"),
                "Gasto con cuenta inexistente (debe fallar)",
                "Efectivo",
                false);

        System.out.println("Intentando insertar con idCuenta=99...");
        try {
            dao.insertar(invalido);
            System.err.println("[FALLO] MySQL acepto una cuenta inexistente.");
        } catch (SQLException e) {
            System.out.println("[OK] MySQL rechazo la insercion por la clave foranea.");
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Codigo  : " + e.getErrorCode());
            System.out.println("Mensaje : " + e.getMessage());
            System.out.println();
            System.out.println("Explicacion: la columna movimientos.id_cuenta tiene la");
            System.out.println("restriccion fk_movimientos_cuenta REFERENCES cuentas(id_cuenta).");
            System.out.println("Como la cuenta 99 no existe, se viola la integridad referencial:");
            System.out.println("MySQL devuelve ERROR 1452 y no inserta ninguna fila.");

            if (e.getErrorCode() != 1452 && !"23000".equals(e.getSQLState())) {
                System.out.println("[AVISO] El codigo no es 1452/23000; revisa el mensaje.");
            }
        }
    }

    /**
     * Intenta insertar un movimiento invalido y muestra el mensaje de la
     * validacion. Si la insercion prosperara, seria un error de la prueba.
     */
    private static void probarValidacion(MovimientoDAO dao, Movimiento movimiento,
                                         String caso) {
        try {
            dao.insertar(movimiento);
            System.err.println("[FALLO] " + caso + ": se acepto un movimiento invalido.");
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] " + caso + " -> " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ERROR SQL] " + caso + " -> " + e.getMessage());
        }
    }
}
