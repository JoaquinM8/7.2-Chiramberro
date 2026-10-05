package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Punto unico de acceso a la base de datos (capa de conexion).
 *
 * Centraliza los datos de configuracion del servidor MySQL/MariaDB de XAMPP
 * y expone la conexion JDBC hacia el resto de la aplicacion.
 *
 * Regla de arquitectura: NADIE mas que esta clase debe crear conexiones.
 * Las capas DAO reciben la conexion ya abierta y se encargan de cerrarla
 * con try-with-resources.
 */
public final class ConexionBD {

    /* =============================================================
       1) DATOS DE CONEXION
       ============================================================= */
    private static final String DRIVER    = "com.mysql.cj.jdbc.Driver";
    private static final String PUERTO    = "3306";
    private static final String BASE      = "escuela_java";
    private static final String USUARIO   = "root";
    private static final String CLAVE     = "";   // en XAMPP el usuario root no tiene clave

    /**
     * URL de conexion. Los parametros evitan advertencias de SSL y fijan la
     * zona horaria para que las fechas viajen correctamente.
     */
    private static final String URL =
            "jdbc:mysql://localhost:" + PUERTO + "/" + BASE
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());

    /** Constructor privado: es una clase utilitaria, no se instancia. */
    private ConexionBD() {
    }

    /* =============================================================
       2) CONEXION
       ============================================================= */

    /**
     * Abre una nueva conexion con el servidor de base de datos.
     *
     * @return una conexion JDBC abierta.
     * @throws SQLException si el servidor no responde, si el usuario o la clave
     *                     son incorrectos, o si la base {@code escuela_java}
     *                     todavia no fue creada.
     */
    public static Connection conectar() throws SQLException {
        try {
            // Carga explicita del driver. Desde JDBC 4.0 podria omitirse,
            // pero se mantiene para que quede claro que clase se usa.
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // Situacion tipica: falta agregar el .jar a la variable de
            // entorno CLASSPATH o a la ruta de compilacion del proyecto.
            LOG.log(Level.SEVERE, "No se encontro el driver " + DRIVER, e);
            throw new SQLException("Falta el driver JDBC. Revisa el archivo lib/mysql-connector-j.jar", e);
        }

        try {
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudo conectar a la base " + BASE, e);
            throw new SQLException("No se pudo conectar a '" + BASE + "'. "
                    + "Verifica que MySQL este iniciado y que hayas ejecutado sql/schema.sql", e);
        }
    }

    /**
     * Comprueba si la base de datos esta disponible, sin lanzar excepciones.
     * La usa la interfaz para avisar con un mensaje claro antes de fallar.
     *
     * @return {@code true} si se pudo abrir la conexion.
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
       3) TEST DE PRUEBA
       Ejecutar esta clase de forma independiente para verificar
       que el driver y la configuracion son correctos.
       ============================================================= */

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println(" TEST DE CONEXION - " + BASE);
        System.out.println("=============================================");
        System.out.println("Driver : " + DRIVER);
        System.out.println("URL    : " + URL);

        if (probarConexion()) {
            System.out.println("RESULTADO: CONEXION EXITOSA");
        } else {
            System.out.println("RESULTADO: FALLO LA CONEXION");
            System.out.println("Revisa que XAMPP/MySQL este iniciado y que");
            System.out.println("el script sql/schema.sql haya sido ejecutado.");
        }
        System.out.println("=============================================");
    }
}