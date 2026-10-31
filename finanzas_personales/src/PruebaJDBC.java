package ar.edu.finanzas;

import ar.edu.finanzas.conexion.ConexionBD;
import ar.edu.finanzas.dao.MovimientoDAO;
import ar.edu.finanzas.modelo.Movimiento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Prueba 1: verifica la conexion JDBC y lista todos los movimientos.
 *
 * Uso:
 *   java -cp "bin;lib/mysql-connector-j-*.jar" ar.edu.finanzas.PruebaJDBC
 */
public class PruebaJDBC {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" PRUEBA JDBC - CONEXION Y SELECT * FROM movimientos");
        System.out.println("=========================================================");

        // ---- 1) Conexion -------------------------------------------------
        try (Connection cx = ConexionBD.conectar()) {
            DatabaseMetaData meta = cx.getMetaData();
            System.out.println("Conectado a : " + meta.getURL());
            System.out.println("Usuario     : " + meta.getUserName());
            System.out.println("Driver      : " + meta.getDriverName()
                    + " " + meta.getDriverVersion());
            System.out.println("Base        : " + cx.getCatalog());
            System.out.println("Conexión establecida correctamente.");
        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXION: " + e.getMessage());
            System.err.println("Revisa que MySQL este iniciado y que exista la base finanzas_personales.");
            return;
        }

        // ---- 2) Lectura -------------------------------------------------
        MovimientoDAO dao = new MovimientoDAO();
        try {
            List<Movimiento> movimientos = dao.listarTodos();

            System.out.println();
            System.out.println("Movimientos encontrados: " + movimientos.size());
            System.out.println("Importe total (suma): "
                    + sumar(movimientos).toPlainString());

            System.out.println();
            imprimirTabla(movimientos);

            if (!movimientos.isEmpty()) {
                System.out.println();
                System.out.println("Primer registro (el mas reciente):");
                System.out.println("  " + movimientos.get(0));
            }
        } catch (SQLException e) {
            System.err.println("ERROR AL LISTAR: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }

    /* =============================================================
       UTILIDADES COMPARTIDAS POR LAS CLASES DE PRUEBA
       ============================================================= */

    /** Suma los importes de una lista (sin distinguir ingreso/gasto). */
    static BigDecimal sumar(List<Movimiento> movimientos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getImporte() != null) {
                total = total.add(movimiento.getImporte());
            }
        }
        return total;
    }

    /** Imprime una tabla legible por consola. */
    static void imprimirTabla(List<Movimiento> movimientos) {
        System.out.printf("%-4s %-10s %-8s %14s %4s %4s %-34s %-20s %-3s%n",
                "ID", "FECHA", "TIPO", "IMPORTE", "CTA", "CAT",
                "DESCRIPCION", "MEDIO DE PAGO", "REC");

        System.out.println("--------------------------------------------------------------------------------"
                + "--------------------------------------------");

        for (Movimiento m : movimientos) {
            System.out.printf("%-4d %-10s %-8s %14s %4d %4d %-34s %-20s %-3s%n",
                    m.getIdMovimiento(),
                    m.getFecha() == null ? "-" : m.getFecha().toString(),
                    m.getTipo(),
                    m.getImporte() == null ? "-" : m.getImporte().toPlainString(),
                    m.getIdCuenta(),
                    m.getIdCategoria(),
                    recortar(m.getDescripcion(), 34),
                    recortar(m.getMedioPago(), 20),
                    m.isRecurrente() ? "SI" : "NO");
        }
    }

    /** Recorta un texto largo para que la tabla no pierda alineacion. */
    static String recortar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo - 1) + ".";
    }
}
