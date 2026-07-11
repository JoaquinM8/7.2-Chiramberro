package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.15 Escribir la función rellenaPares () a la que se le pasa como  parámetro  una  tabla que debe rellenar de la siguiente forma: se leerá por teclado una serie de números, guardando en la tabla los pares hasta que esté llena, e ignorando los impares. La función tiene que devolver la cantidad de impares desechados.

public class ejercicio5_15 extends JPanel {

    int pares[] = new int[5];
    int cantidadPares = 0;
    int impares = 0;

    public ejercicio5_15() {

        JLabel titleLabel = new JLabel("Rellenar Pares", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese numeros. Se guardan solo pares.\n");

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                rellenaPares(pares, num);
                mostrar(resultArea);

                if (cantidadPares == pares.length) {
                    resultArea.append("\nCantidad de impares desechados: " + impares);
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

    public int rellenaPares(int tabla[], int num) {
        if (num % 2 == 0) {
            tabla[cantidadPares] = num;
            cantidadPares++;
        } else {
            impares++;
        }

        return impares;
    }

    public void mostrar(JTextArea resultArea) {
        resultArea.setText("Pares guardados:\n");
        for (int i = 0; i < cantidadPares; i++) {
            resultArea.append(pares[i] + "\n");
        }
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
        JFrame frame = new JFrame("Ejercicio 5.15");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_15());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_15::mostrarEnVentana);
    }
}
