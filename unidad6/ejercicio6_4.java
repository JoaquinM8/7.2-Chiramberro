package unidad6;

import javax.swing.*;
import java.awt.*;


// 6.4. Diseñar una aplicación que pida al usuario que introduzca una frase por teclado e indique cuántos espacios en blanco tiene.

public class ejercicio6_4 extends JPanel {

    public ejercicio6_4() {

        JLabel titleLabel = new JLabel("Contar espacios", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(20);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String frase = inputField.getText();
            int espacios = 0;

            for (int i = 0; i < frase.length(); i++) {
                if (frase.charAt(i) == ' ') {
                    espacios++;
                }
            }

            resultArea.setText("Cantidad de espacios: " + espacios);
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            JPanel leftPanel = new JPanel(new BorderLayout());
                JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                    inputPanel.add(new JLabel("Frase:"));
                    inputPanel.add(inputField);
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
        JFrame frame = new JFrame("Ejercicio 6.4");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_4());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_4::mostrarEnVentana);
    }
}
