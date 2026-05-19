package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_9 extends JFrame {

    public ejercicio3_9() {
        setTitle("Ejercicio 3.9");
        setSize(720,480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ejercicio3_9());
    }
}