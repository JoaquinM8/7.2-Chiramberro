package unidad6;

import javax.swing.*;
import java.awt.*;

// 6.13. Diseñar un algoritmo que lea del teclado una frase e indique, para la letras que aparecen en la frase, cuántas veces se repite cada una. Se consideran iguales las letras mayúsculas y las minúsculas para realizar la cuenta.
public class ejercicio6_13 extends JPanel {

    public ejercicio6_13() {

        JLabel titleLabel = new JLabel("Repeticion de letras", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(25);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Contar");

        JTextArea resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String frase = inputField.getText().toLowerCase();
            resultArea.setText("");

            for (char letra = 'a'; letra <= 'z'; letra++) {
                int cantidad = 0;

                for (int i = 0; i < frase.length(); i++) {
                    if (frase.charAt(i) == letra) {
                        cantidad++;
                    }
                }

                if (cantidad > 0) {
                    resultArea.append(letra + ": " + cantidad + " veces\n");
                }
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        inputPanel.add(inputField);
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
        JFrame frame = new JFrame("Ejercicio 6.13");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_13());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_13::mostrarEnVentana);
    }
}
