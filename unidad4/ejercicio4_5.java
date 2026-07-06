package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_5 extends JPanel {

    public ejercicio4_5() {
        JLabel titleLabel = new JLabel("MAXIMO DE 3 NUMEROS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField1 = new JTextField(5);
        JTextField inputField2 = new JTextField(5);
        JTextField inputField3 = new JTextField(5);
        inputField1.setHorizontalAlignment(SwingConstants.CENTER);
        inputField2.setHorizontalAlignment(SwingConstants.CENTER);
        inputField3.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Calcular");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            try {
                int a = Integer.parseInt(inputField1.getText());
                int b = Integer.parseInt(inputField2.getText());
                int c = Integer.parseInt(inputField3.getText());
                resultLabel.setText("Mayor: " + maximo(a, b, c));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultLabel, "Ingrese numeros enteros", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Numero 1:"));
        inputPanel.add(inputField1);
        inputPanel.add(new JLabel("Numero 2:"));
        inputPanel.add(inputField2);
        inputPanel.add(new JLabel("Numero 3:"));
        inputPanel.add(inputField3);
        inputPanel.add(submitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static int maximo(int a, int b, int c) {
        int mayor = a;

        if (b > mayor) {
            mayor = b;
        }
        if (c > mayor) {
            mayor = c;
        }

        return mayor;
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.5");
        frame.setSize(650, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_5());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_5::mostrarEnVentana);
    }
}
