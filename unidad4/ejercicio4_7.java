package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_7 extends JPanel {

    public ejercicio4_7() {
        JLabel titleLabel = new JLabel("NUMERO PRIMO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Verificar");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(inputField.getText());
                if (esPrimo(n)) {
                    resultLabel.setText(n + " es primo");
                } else {
                    resultLabel.setText(n + " no es primo");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultLabel, "Ingrese un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Numero:"));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static boolean esPrimo(int n) {
        if (n < 2) {
            return false;
        }

        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.7");
        frame.setSize(480, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_7());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_7::mostrarEnVentana);
    }
}
