package ar.edu.finanzas;

import ar.edu.finanzas.dao.MovimientoDAO;
import ar.edu.finanzas.modelo.Movimiento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Prueba 2: inserta un movimiento valido y comprueba que la validacion previa
 * rechace los datos incorrectos antes de llegar a MySQL.
 *
 * Uso:
 *   java -cp "bin;lib/mysql-connector-j-*.jar" ar.edu.finanzas.PruebaInsercion
 */
public class PruebaInsercion {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" PRUEBA DE INSERCION Y VALIDACION - movimientos");
        System.out.println("=========================================================");

        MovimientoDAO dao = new MovimientoDAO();

        // ---- 1) Alta valida ---------------------------------------------
        // idCuenta=1 e idCategoria=4 existen en los datos de la Entrega 1.
        Movimiento nuevo = new Movimiento(
                1,                                 // id_cuenta
                4,                                 // id_categoria (Alimentacion)
                "GASTO",
                LocalDate.now(),
                new BigDecimal("12345.67"),
                "Compra de prueba desde Java (Entrega 2)",
                "Efectivo",
                false);

        System.out.println();
        System.out.println("Insertando: " + nuevo);

        try {
            boolean insertado = dao.insertar(nuevo);

            if (insertado) {
                System.out.println("RESULTADO: insercion exitosa.");
                System.out.println("ID autogenerado por MySQL: " + nuevo.getIdMovimiento());

                // Se vuelve a leer para comprobar que el registro quedo guardado.
                List<Movimiento> todos = dao.listarTodos();
                System.out.println("Total de movimientos ahora: " + todos.size());

                int hasta = Math.min(5, todos.size());
                System.out.println();
                System.out.println("Ultimos 5 movimientos (por fecha descendente):");
                PruebaJDBC.imprimirTabla(todos.subList(0, hasta));
            } else {
                System.out.println("RESULTADO: la base no inserto ninguna fila.");
            }
        } catch (SQLException e) {
            System.err.println("ERROR SQL AL INSERTAR: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Revisa que existan la cuenta 1 y la categoria 4.");
        }

        // ---- 2) Casos invalidos -----------------------------------------
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
