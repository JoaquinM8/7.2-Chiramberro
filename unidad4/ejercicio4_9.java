package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_9 extends JPanel {

    public ejercicio4_9() {
        JLabel titleLabel = new JLabel("DIVISORES PRIMOS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Mostrar");

        JTextArea resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(inputField.getText());
                resultArea.setText(divisoresPrimos(n));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultArea, "Ingrese un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
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
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static String divisoresPrimos(int n) {
        String texto = "";

        for (int i = 2; i <= n; i++) {
            if (n % i == 0 && esPrimo(i)) {
                texto = texto + i + "\n";
            }
        }

        return texto;
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
        JFrame frame = new JFrame("Ejercicio 4.9");
        frame.setSize(480, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_9());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_9::mostrarEnVentana);
    }
}
