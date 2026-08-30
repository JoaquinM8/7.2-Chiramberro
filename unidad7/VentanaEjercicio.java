package unidad7;

import javax.swing.*;

public class VentanaEjercicio {
    public static void mostrar(String titulo, JPanel ejercicio) {
        JFrame ventana = new JFrame(titulo);
        ventana.setSize(650, 480);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(ejercicio);
        ventana.setVisible(true);
    }
}
