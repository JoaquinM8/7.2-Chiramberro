import vista.FinanzasFrame;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Genera capturas PNG de las tres pestanas de la aplicacion.
 *
 * <p>La ventana se muestra brevemente y se pinta en una imagen. Esto permite
 * dejar evidencia visual de la entrega sin depender de una herramienta externa
 * de captura de pantalla.</p>
 *
 * <p>Uso:</p>
 * <pre>
 *   java -cp "bin;lib/mysql-connector-j-26.7.0.jar" CapturaGUI
 * </pre>
 */
public class CapturaGUI {

    public static void main(String[] args) throws Exception {
        System.setProperty("finanzas.captura", "true");

        final FinanzasFrame[] referencia = new FinanzasFrame[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());

                FinanzasFrame frame = new FinanzasFrame();
                frame.setSize(1180, 760);
                frame.setLocation(30, 30);

                // Periodo de ejemplo: marzo 2026, donde estan los datos cargados.
                frame.configurarResumen(2026, 3);
                frame.configurarPresupuestos(1, 2026, 3);
                frame.seleccionarPrimeraFilaMovimientos();

                referencia[0] = frame;
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Dejar que Swing complete el layout y el primer repintado.
        Thread.sleep(1200);

        File carpeta = new File("entregas/entrega4/capturas");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IllegalStateException(
                    "No se pudo crear la carpeta " + carpeta.getAbsolutePath());
        }

        capturar(referencia[0], new File(carpeta, "01_ventana_movimientos.png"));

        SwingUtilities.invokeAndWait(() -> referencia[0].seleccionarPestana(1));
        Thread.sleep(500);
        capturar(referencia[0], new File(carpeta, "02_resumen_mensual.png"));

        SwingUtilities.invokeAndWait(() -> referencia[0].seleccionarPestana(2));
        Thread.sleep(500);
        capturar(referencia[0], new File(carpeta, "03_presupuestos.png"));

        SwingUtilities.invokeAndWait(() -> referencia[0].dispose());
        System.exit(0);
    }

    private static void capturar(FinanzasFrame frame, File archivo)
            throws Exception {

        BufferedImage imagen = new BufferedImage(
                frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

        SwingUtilities.invokeAndWait(() -> {
            Graphics2D g = imagen.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            frame.printAll(g);
            g.dispose();
        });

        ImageIO.write(imagen, "png", archivo);
        System.out.println("Captura generada: " + archivo.getPath()
                + " (" + frame.getWidth() + "x" + frame.getHeight() + ")");
    }
}
