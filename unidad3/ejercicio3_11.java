package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_11 extends JPanel {

    public ejercicio3_11() {

        JPanel mainPanel = new JPanel(new GridLayout(11,11,5,5));

        for (int i = 0; i <= 10; ++i) {
            for (int j = 0; j <= 10; ++j) {
                JLabel label;
                if (i == 0) {
                    if (j == 0) {
                        label = new JLabel("", SwingConstants.CENTER);
                        label.setOpaque(true);
                        label.setBackground(new Color(230, 230, 230)); 
                    } else {
                        label = new JLabel("" + j, SwingConstants.CENTER);
                        label.setBackground(new Color(210, 210, 210));
                        label.setFont(new Font("Arial", Font.BOLD, 25)); 
                    }
                } else {
                    if (j == 0) {
                        label = new JLabel("" + i, SwingConstants.CENTER);
                        label.setBackground(new Color(210, 210, 210));
                        label.setFont(new Font("Arial", Font.BOLD, 25)); 
                    } else {
                        label = new JLabel("" + i*j, SwingConstants.CENTER);
                        label.setBackground(new Color(150, 150, 150));
                        label.setFont(new Font("Arial", Font.PLAIN, 18)); 
                    }
                }
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                mainPanel.add(label);
            }
        }
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 3.11");
        frame.setSize(720, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_11());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_11::mostrarEnVentana);
    }
}