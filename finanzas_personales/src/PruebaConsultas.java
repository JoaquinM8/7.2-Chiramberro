package ar.edu.finanzas;

import ar.edu.finanzas.dao.MovimientoDAO;
import ar.edu.finanzas.modelo.Movimiento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Prueba 3: ejercita las consultas con filtros del DAO.
 *
 * Uso:
 *   java -cp "bin;lib/mysql-connector-j-*.jar" ar.edu.finanzas.PruebaConsultas
 */
public class PruebaConsultas {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" PRUEBAS DE CONSULTAS CON PARAMETROS");
        System.out.println("=========================================================");

        MovimientoDAO dao = new MovimientoDAO();

        try {
            // 1) listarPorTipo: se pasa en minusculas a proposito para mostrar
            //    que el DAO normaliza el valor antes de consultar.
            mostrar("1) Movimientos de tipo 'gasto'",
                    dao.listarPorTipo("gasto"));

            // 2) buscarPorFecha: marzo 2026, extremos incluidos.
            mostrar("2) Movimientos entre 2026-03-01 y 2026-03-31",
                    dao.buscarPorFecha(LocalDate.of(2026, 3, 1),
                            LocalDate.of(2026, 3, 31)));

            // 3) listarGastosMayoresA: importe estrictamente mayor a 100.000.
            mostrar("3) Gastos mayores a 100000.00",
                    dao.listarGastosMayoresA(new BigDecimal("100000.00")));

            // 4) Validacion de rangos invertidos.
            System.out.println();
            System.out.println("---------------------------------------------------------");
            System.out.println(" VALIDACIONES DE PARAMETROS");
            System.out.println("---------------------------------------------------------");

            probarRangoInvertido(dao);
            probarTipoInvalido(dao);

        } catch (SQLException e) {
            System.err.println("ERROR SQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }

    /** Imprime un titulo, la cantidad, el total y la tabla de resultados. */
    private static void mostrar(String titulo, List<Movimiento> movimientos) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
        System.out.println("Cantidad: " + movimientos.size());
        System.out.println("Importe total: " + PruebaJDBC.sumar(movimientos).toPlainString());
        System.out.println();
        PruebaJDBC.imprimirTabla(movimientos);
    }

    private static void probarRangoInvertido(MovimientoDAO dao) {
        try {
            dao.buscarPorFecha(LocalDate.of(2026, 3, 31),
                    LocalDate.of(2026, 3, 1));
            System.err.println("[FALLO] Se acepto un rango de fechas invertido.");
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] rango invertido -> " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ERROR SQL] " + e.getMessage());
        }
    }

    private static void probarTipoInvalido(MovimientoDAO dao) {
        try {
            dao.listarPorTipo("TRANSFERENCIA");
            System.err.println("[FALLO] Se acepto un tipo invalido.");
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] tipo invalido -> " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ERROR SQL] " + e.getMessage());
        }
    }
}
