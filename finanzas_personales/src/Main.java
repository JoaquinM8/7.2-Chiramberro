import vista.FinanzasFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion de escritorio.
 *
 * <p>Swing debe crear y actualizar sus componentes en el Event Dispatch
 * Thread. Por eso la ventana se construye dentro de
 * {@link SwingUtilities#invokeLater(Runnable)}.</p>
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Se mantiene el estilo predeterminado de Swing.
            }

            new FinanzasFrame().setVisible(true);
        });
    }
}
