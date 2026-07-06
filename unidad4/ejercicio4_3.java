package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_3 extends JPanel {

    public ejercicio4_3() {
        JLabel titleLabel = new JLabel("CILINDRO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField opcionField = new JTextField(4);
        JTextField radioField = new JTextField(5);
        JTextField alturaField = new JTextField(5);
        opcionField.setHorizontalAlignment(SwingConstants.CENTER);
        radioField.setHorizontalAlignment(SwingConstants.CENTER);
        alturaField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Calcular");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            try {
                int opcion = Integer.parseInt(opcionField.getText());
                double radio = Double.parseDouble(radioField.getText());
                double altura = Double.parseDouble(alturaField.getText());
                double resultado = calcularCilindro(opcion, radio, altura);

                if (opcion == 1) {
                    resultLabel.setText("Area: " + resultado);
                } else if (opcion == 2) {
                    resultLabel.setText("Volumen: " + resultado);
                } else {
                    resultLabel.setText("Opcion incorrecta");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultLabel, "Ingrese valores correctos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("1 Area / 2 Volumen:"));
        inputPanel.add(opcionField);
        inputPanel.add(new JLabel("Radio:"));
        inputPanel.add(radioField);
        inputPanel.add(new JLabel("Altura:"));
        inputPanel.add(alturaField);
        inputPanel.add(submitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static double calcularCilindro(int opcion, double radio, double altura) {
        if (opcion == 1) {
            return 2 * Math.PI * radio * (altura + radio);
        } else if (opcion == 2) {
            return Math.PI * radio * radio * altura;
        } else {
            return 0;
        }
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.3");
        frame.setSize(650, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_3());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_3::mostrarEnVentana);
    }
}
