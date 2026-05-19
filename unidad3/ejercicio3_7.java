package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_7 extends JFrame {

    public ejercicio3_7() {
        setTitle("Ejercicio 3.7");
        setSize(720,480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ejercicio3_7());
    }
}