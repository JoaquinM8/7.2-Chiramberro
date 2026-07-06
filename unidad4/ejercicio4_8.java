package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_8 extends JPanel {

    public ejercicio4_8() {
        JLabel titleLabel = new JLabel("CANTIDAD DE DIVISORES PRIMOS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Calcular");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(inputField.getText());
                resultLabel.setText("Cantidad: " + divisoresPrimos(n));
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

    public static int divisoresPrimos(int n) {
        int cantidad = 0;

        for (int i = 2; i <= n; i++) {
            if (n % i == 0 && esPrimo(i)) {
                cantidad++;
            }
        }

        return cantidad;
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
        JFrame frame = new JFrame("Ejercicio 4.8");
        frame.setSize(560, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_8());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_8::mostrarEnVentana);
    }
}
