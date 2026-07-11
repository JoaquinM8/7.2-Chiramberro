package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.10. Leer y almacenar n (ingresar por teclado la cantidad) números enteros en una tabla, a partir de la que se construirán otras dos tablas con los elementos con valores pares e impares de la primera, respectivamente.

public class ejercicio5_10 extends JPanel {

    int numeros[];
    int i = 0;
    boolean cantidadIngresada = false;

    public ejercicio5_10() {

        JLabel titleLabel = new JLabel("Pares e Impares", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese la cantidad de numeros.\n");

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());

                if (cantidadIngresada == false) {
                    if (num > 0) {
                        numeros = new int[num];
                        cantidadIngresada = true;
                        resultArea.setText("Ingrese numero 1.\n");
                    } else {
                        JOptionPane.showMessageDialog(resultArea, "Cantidad invalida", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    numeros[i] = num;
                    i++;

                    if (i == numeros.length) {
                        mostrarTablas(resultArea);
                        inputField.setEditable(false);
                        submitButton.setEnabled(false);
                    } else {
                        resultArea.append("Ingrese numero " + (i + 1) + ".\n");
                    }
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

    public void mostrarTablas(JTextArea resultArea) {
        resultArea.setText("Pares:\n");
        for (int i = 0; i < numeros.length; i++) {
            if (numeros[i] % 2 == 0) {
                resultArea.append(numeros[i] + " ");
            }
        }

        resultArea.append("\n\nImpares:\n");
        for (int i = 0; i < numeros.length; i++) {
            if (numeros[i] % 2 != 0) {
                resultArea.append(numeros[i] + " ");
            }
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
        JFrame frame = new JFrame("Ejercicio 5.10");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_10());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_10::mostrarEnVentana);
    }
}
