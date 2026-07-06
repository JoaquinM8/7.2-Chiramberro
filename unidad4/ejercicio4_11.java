package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_11 extends JPanel {

    public ejercicio4_11() {
        JLabel titleLabel = new JLabel("POTENCIA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField baseField = new JTextField(5);
        JTextField exponenteField = new JTextField(5);
        baseField.setHorizontalAlignment(SwingConstants.CENTER);
        exponenteField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Calcular");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            try {
                double a = Double.parseDouble(baseField.getText());
                int n = Integer.parseInt(exponenteField.getText());

                if (n >= 0) {
                    resultLabel.setText("Resultado: " + potencia(a, n));
                } else {
                    resultLabel.setText("El exponente debe ser positivo");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultLabel, "Ingrese valores correctos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Base:"));
        inputPanel.add(baseField);
        inputPanel.add(new JLabel("Exponente:"));
        inputPanel.add(exponenteField);
        inputPanel.add(submitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static double potencia(double a, int n) {
        double resultado = 1;

        for (int i = 1; i <= n; i++) {
            resultado = resultado * a;
        }

        return resultado;
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.11");
        frame.setSize(560, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_11());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_11::mostrarEnVentana);
    }
}
