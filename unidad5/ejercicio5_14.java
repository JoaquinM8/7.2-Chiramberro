package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.14 Definir una función que tome como parámetros dos tablas, la primera con los 6 números de una apuesta de la primitiva, y la segunda con los 6 números de la combinación ganadora. La función devolverá el número de aciertos

public class ejercicio5_14 extends JPanel {

    int apuesta[] = new int[6];
    int ganadora[] = new int[6];
    int i = 0;

    public ejercicio5_14() {

        JLabel titleLabel = new JLabel("Primitiva", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese numero 1 de la apuesta.\n");

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());

                if (i < 6) {
                    apuesta[i] = num;
                    i++;
                    resultArea.append("Ingrese numero " + (i + 1) + " de la apuesta.\n");
                } else if (i < 12) {
                    ganadora[i - 6] = num;
                    i++;
                    resultArea.append("Ingrese numero " + (i - 5) + " de la ganadora.\n");
                }

                if (i == 6) {
                    resultArea.setText("Apuesta cargada. Ingrese numero 1 de la ganadora.\n");
                }

                if (i == 12) {
                    resultArea.setText("Cantidad de aciertos: " + aciertos(apuesta, ganadora));
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                }

                inputField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = crearPanel(titleLabel, inputField, submitButton, resultArea);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static int aciertos(int apuesta[], int ganadora[]) {
        int cantidad = 0;

        for (int i = 0; i < apuesta.length; i++) {
            for (int j = 0; j < ganadora.length; j++) {
                if (apuesta[i] == ganadora[j]) {
                    cantidad++;
                }
            }
        }

        return cantidad;
    }

    public JPanel crearPanel(JLabel titleLabel, JTextField inputField, JButton submitButton, JTextArea resultArea) {
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
        return mainPanel;
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 5.14");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_14());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_14::mostrarEnVentana);
    }
}
