package unidad6;

import javax.swing.*;
import java.awt.*;


// 6.1. Introducir por teclado dos palabras e indicar cuál de ellas es la más corta, es decir, la que contiene menos caracteres.

public class ejercicio6_1 extends JPanel {

    public ejercicio6_1() {

        JLabel titleLabel = new JLabel("Palabra mas corta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField palabra1Field = new JTextField(10);
        palabra1Field.setFont(new Font("Arial", Font.PLAIN, 15));
        palabra1Field.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField palabra2Field = new JTextField(10);
        palabra2Field.setFont(new Font("Arial", Font.PLAIN, 15));
        palabra2Field.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String palabra1 = palabra1Field.getText();
            String palabra2 = palabra2Field.getText();

            if (palabra1.length() < palabra2.length()) {
                resultArea.setText("La palabra mas corta es: " + palabra1);
            } else if (palabra2.length() < palabra1.length()) {
                resultArea.setText("La palabra mas corta es: " + palabra2);
            } else {
                resultArea.setText("Las dos palabras tienen la misma longitud.");
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            JPanel leftPanel = new JPanel(new BorderLayout(5,5));
                JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                    inputPanel.add(new JLabel("Palabra 1:"));
                    inputPanel.add(palabra1Field);
                    inputPanel.add(new JLabel("Palabra 2:"));
                    inputPanel.add(palabra2Field);
                    inputPanel.add(new JLabel(""));
                    inputPanel.add(submitButton);
                leftPanel.add(titleLabel, BorderLayout.NORTH);
                leftPanel.add(inputPanel, BorderLayout.CENTER);
            JPanel rightPanel = new JPanel();
                rightPanel.add(new JScrollPane(resultArea));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 20);
            mainPanel.add(leftPanel, gbc);
            gbc.gridx = 1;
            gbc.insets = new Insets(5, 5, 5, 5);
            mainPanel.add(rightPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 6.1");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_1());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_1::mostrarEnVentana);
    }
}
