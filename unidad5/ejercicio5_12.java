package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.12 Crear una tabla bidimensional de tamaño 5 x 5 y rellenarla de la siguiente forma: la posición [n, m] debe contener n + m. Después se debe mostrar su contenido

public class ejercicio5_12 extends JPanel {

    public ejercicio5_12() {

        JLabel titleLabel = new JLabel("Tabla 5 x 5", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);

        int tabla[][] = new int[5][5];
        rellenar(tabla);
        mostrar(tabla, resultArea);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void rellenar(int tabla[][]) {
        for (int n = 0; n < 5; n++) {
            for (int m = 0; m < 5; m++) {
                tabla[n][m] = n + m;
            }
        }
    }

    public static void mostrar(int tabla[][], JTextArea resultArea) {
        for (int n = 0; n < 5; n++) {
            for (int m = 0; m < 5; m++) {
                resultArea.append(tabla[n][m] + " ");
            }
            resultArea.append("\n");
        }
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 5.12");
        frame.setSize(480, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_12());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_12::mostrarEnVentana);
    }
}
