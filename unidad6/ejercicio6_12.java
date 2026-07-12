package unidad6;

import javax.swing.*;
import java.awt.*;

// 6.12. Un anagrama es un palabra, o frase, que resulta de la transposición de otra palabra o frase. Ejemplos de anagramas para la palabra roma son: amor, ramo o mora. Construir un programa que solicite al usuario dos palabras e indique si son anagramas una de otra.
public class ejercicio6_12 extends JPanel {

    public ejercicio6_12() {

        JLabel titleLabel = new JLabel("Anagramas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField palabra1Field = new JTextField(10);
        palabra1Field.setFont(new Font("Arial", Font.PLAIN, 15));
        palabra1Field.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField palabra2Field = new JTextField(10);
        palabra2Field.setFont(new Font("Arial", Font.PLAIN, 15));
        palabra2Field.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Comprobar");

        JTextArea resultArea = new JTextArea(8, 20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String palabra1 = palabra1Field.getText().toLowerCase();
            String palabra2 = palabra2Field.getText().toLowerCase();
            boolean anagrama = palabra1.length() == palabra2.length();

            for (int i = 0; i < palabra1.length() && anagrama; i++) {
                int cantidad1 = 0;
                int cantidad2 = 0;

                for (int j = 0; j < palabra1.length(); j++) {
                    if (palabra1.charAt(j) == palabra1.charAt(i)) {
                        cantidad1++;
                    }
                    if (palabra2.charAt(j) == palabra1.charAt(i)) {
                        cantidad2++;
                    }
                }

                if (cantidad1 != cantidad2) {
                    anagrama = false;
                }
            }

            if (anagrama) {
                resultArea.setText("Son anagramas.");
            } else {
                resultArea.setText("No son anagramas.");
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = new JPanel(new BorderLayout());
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
        JFrame frame = new JFrame("Ejercicio 6.12");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_12());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_12::mostrarEnVentana);
    }
}
