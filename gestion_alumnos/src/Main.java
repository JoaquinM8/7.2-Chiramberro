import conexion.ConexionBD;
import vista.FrmAlumnos;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion "Gestion de Alumnos".
 *
 * Responsabilidades de la clase Main en una aplicacion Swing:
 *   1. elegir el aspecto (look and feel) de los componentes;
 *   2. comprobar que la base de datos este disponible;
 *   3. abrir la ventana principal desde el hilo de eventos de Swing.
 *
 * REGLA DE ORO DE SWING: la interfaz nunca se construye en el hilo principal.
 * Todo lo grafico se hace dentro de SwingUtilities.invokeLater.
 */
public class Main {

    public static void main(String[] args) {

        // 1) Aspecto visual: usa el que se vea mejor en la plataforma.
        //    Se aplica antes de crear cualquier componente.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, seguimos con el look and feel por defecto de Java.
            System.err.println("No se pudo aplicar el aspecto del sistema: " + e.getMessage());
        }

        // 2) La interfaz se construye en el hilo de eventos (EDT).
        SwingUtilities.invokeLater(() -> {

            // 3) Verificamos la base antes de abrir la ventana, para no
            //    dejar al usuario frente a una pantalla vacia sin explicar.
            if (!ConexionBD.probarConexion()) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo conectar con la base de datos 'escuela_java'.\n\n"
                      + "Revisá que:\n"
                      + "  1) XAMPP (MySQL) esté iniciado.\n"
                      + "  2) Hayas ejecutado el script sql/schema.sql.\n"
                      + "  3) Usuario 'root' y clave vacía.",
                        "Sin conexión a la base de datos",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return;
            }

            new FrmAlumnos().setVisible(true);
        });
    }
}