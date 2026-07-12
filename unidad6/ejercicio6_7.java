package unidad6;

import javax.swing.*;
import java.awt.*;


// 6.7. Diseñar un programa que solicite al usuario una frase y una palabra. A continuación buscará cuántas veces aparece la palabra en la frase.

public class ejercicio6_7 extends JPanel {

    public ejercicio6_7() {

        JLabel titleLabel = new JLabel("Buscar palabra", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField fraseField = new JTextField(20);
        fraseField.setFont(new Font("Arial", Font.PLAIN, 15));
        fraseField.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField palabraField = new JTextField(10);
        palabraField.setFont(new Font("Arial", Font.PLAIN, 15));
        palabraField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String frase = fraseField.getText();
            String palabra = palabraField.getText();
            int cantidad = 0;
            int pos = 0;

            while ((pos = frase.toLowerCase().indexOf(palabra.toLowerCase(), pos)) != -1) {
                cantidad++;
                pos += palabra.length();
            }

            resultArea.setText("Aparece " + cantidad + " veces.");
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        inputPanel.add(new JLabel("Frase:"));
        inputPanel.add(fraseField);
        inputPanel.add(new JLabel("Palabra:"));
        inputPanel.add(palabraField);
        inputPanel.add(submitButton);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(inputPanel, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel();
        rightPanel.add(new JScrollPane(resultArea));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 20);
        mainPanel.add(leftPanel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(rightPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 6.7");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_7());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_7::mostrarEnVentana);
    }
}
