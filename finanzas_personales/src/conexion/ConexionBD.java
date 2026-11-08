package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Capa de conexion: unico punto de acceso a la base {@code finanzas_personales}
 * del MySQL/MariaDB de XAMPP.
 *
 * Regla de arquitectura: ninguna otra clase crea conexiones. El DAO pide una
 * conexion a esta clase y la cierra con try-with-resources.
 */
public final class ConexionBD {

    /* =============================================================
       1) DATOS DE CONEXION
       ============================================================= */
    private static final String DRIVER  = "com.mysql.cj.jdbc.Driver";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "";   // en XAMPP el usuario root no tiene clave

    /**
     * URL local de XAMPP. useSSL=false evita advertencias de SSL y
     * serverTimezone=UTC mantiene las fechas estables al viajar por JDBC.
     */
    private static final String URL =
            "jdbc:mysql://localhost:3306/finanzas_personales"
          + "?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());

    /** Constructor privado: clase utilitaria, no se instancia. */
    private ConexionBD() {
    }

    /* =============================================================
       2) CONEXION
       ============================================================= */

    /**
     * Abre una conexion JDBC nueva.
     *
     * @return conexion abierta contra {@code finanzas_personales}.
     * @throws SQLException si MySQL no esta iniciado, si la base no existe o
     *                      si el driver no esta en el classpath.
     */
    public static Connection conectar() throws SQLException {
        try {
            // Carga explicita del driver (JDBC 4.0+ podria omitirse, pero deja
            // claro que implementacion se esta usando).
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "No se encontro el driver " + DRIVER, e);
            throw new SQLException(
                    "Falta el driver JDBC. Agrega mysql-connector-j-*.jar a la carpeta lib/.",
                    e);
        }

        try {
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudo conectar a finanzas_personales", e);
            throw new SQLException(
                    "No se pudo conectar a 'finanzas_personales'. "
                    + "Verifica que MySQL este iniciado en XAMPP y que hayas ejecutado sql/01_esquema.sql.",
                    e);
        }
    }

    /**
     * Prueba la conexion sin propagar la excepcion. La usan las clases de
     * prueba para mostrar un mensaje claro cuando la base no esta disponible.
     *
     * @return {@code true} si la conexion se abrio correctamente.
     */
    public static boolean probarConexion() {
        try (Connection cx = conectar()) {
            return cx != null && !cx.isClosed();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Prueba de conexion fallida", e);
            return false;
        }
    }

    /* =============================================================
       3) TEST INDEPENDIENTE
       ============================================================= */

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println(" TEST DE CONEXION - finanzas_personales");
        System.out.println("=============================================");
        System.out.println("Driver : " + DRIVER);
        System.out.println("URL    : " + URL);

        if (probarConexion()) {
            System.out.println("Conexión establecida correctamente.");
        } else {
            System.out.println("RESULTADO: FALLO LA CONEXION");
            System.out.println("Revisa XAMPP/MySQL y el script sql/01_esquema.sql.");
        }
        System.out.println("=============================================");
    }
}
