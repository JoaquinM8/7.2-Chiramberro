package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.7. Leer una serie de 6 enteros que se almacenarán en una tabla que hay que ordenar y mostrar. Leer otra serie de 6 enteros, que también se guardarán en una tabla y se mostrarán ordenados. A continuación, fusionar las dos tablas en una tercera, de forma que los 12 números sigan ordenados. Fusionar dos tablas ordenadas significa copiar en el orden correcto para que los datos resultantes continúen ordenados, sin necesidad de volver a realizar una ordenación.

public class ejercicio5_7 extends JPanel {

    int tabla1[] = new int[6];
    int tabla2[] = new int[6];
    int i = 0;

    public ejercicio5_7() {

        JLabel titleLabel = new JLabel("Fusionar Tablas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese numero 1 de la primera tabla.\n");

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());

                if (i < 6) {
                    tabla1[i] = num;
                    i++;
                    resultArea.append("Ingrese numero " + (i + 1) + " de la primera tabla.\n");
                } else if (i < 12) {
                    tabla2[i - 6] = num;
                    i++;
                    resultArea.append("Ingrese numero " + (i - 5) + " de la segunda tabla.\n");
                }

                if (i == 6) {
                    resultArea.setText("Primera tabla completa. Ingrese numero 1 de la segunda tabla.\n");
                }

                if (i == 12) {
                    ordenar(tabla1);
                    ordenar(tabla2);
                    int fusion[] = fusionar(tabla1, tabla2);
                    mostrarTodo(resultArea, fusion);
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

    public static void ordenar(int tabla[]) {
        for (int i = 0; i < tabla.length - 1; i++) {
            for (int j = 0; j < tabla.length - 1 - i; j++) {
                if (tabla[j] > tabla[j + 1]) {
                    int aux = tabla[j];
                    tabla[j] = tabla[j + 1];
                    tabla[j + 1] = aux;
                }
            }
        }
    }

    public static int[] fusionar(int tabla1[], int tabla2[]) {
        int resultado[] = new int[12];
        int i = 0;
        int j = 0;
        int k = 0;

        while (i < tabla1.length && j < tabla2.length) {
            if (tabla1[i] <= tabla2[j]) {
                resultado[k] = tabla1[i];
                i++;
            } else {
                resultado[k] = tabla2[j];
                j++;
            }
            k++;
        }

        while (i < tabla1.length) {
            resultado[k] = tabla1[i];
            i++;
            k++;
        }

        while (j < tabla2.length) {
            resultado[k] = tabla2[j];
            j++;
            k++;
        }

        return resultado;
    }

    public void mostrarTodo(JTextArea resultArea, int fusion[]) {
        resultArea.setText("Tabla 1 ordenada:\n");
        for (int i = 0; i < tabla1.length; i++) {
            resultArea.append(tabla1[i] + " ");
        }

        resultArea.append("\n\nTabla 2 ordenada:\n");
        for (int i = 0; i < tabla2.length; i++) {
            resultArea.append(tabla2[i] + " ");
        }

        resultArea.append("\n\nFusion:\n");
        for (int i = 0; i < fusion.length; i++) {
            resultArea.append(fusion[i] + " ");
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
        JFrame frame = new JFrame("Ejercicio 5.7");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_7());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_7::mostrarEnVentana);
    }
}
