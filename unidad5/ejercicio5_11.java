package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.11 Escribir un programa que solicite los elementos de una matriz de tamaño 4 x 4. La aplicación debe decidir si la matriz introducida corresponde a una matriz mágica, que es aquella donde la suma de lo elementos de cualquier fila o de cualquier columna vale lo mismo.

public class ejercicio5_11 extends JPanel {

    int matriz[][] = new int[4][4];
    int contador = 0;

    public ejercicio5_11() {

        JLabel titleLabel = new JLabel("Matriz Magica", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese valor de fila 1, columna 1.\n");

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                int fila = contador / 4;
                int columna = contador % 4;
                matriz[fila][columna] = num;
                contador++;

                if (contador == 16) {
                    mostrarMatriz(resultArea);
                    if (esMagica(matriz)) {
                        resultArea.append("\nLa matriz es magica.");
                    } else {
                        resultArea.append("\nLa matriz no es magica.");
                    }
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                } else {
                    fila = contador / 4;
                    columna = contador % 4;
                    resultArea.append("Ingrese valor de fila " + (fila + 1) + ", columna " + (columna + 1) + ".\n");
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

    public static boolean esMagica(int matriz[][]) {
        int sumaBase = 0;

        for (int j = 0; j < 4; j++) {
            sumaBase += matriz[0][j];
        }

        for (int i = 0; i < 4; i++) {
            int sumaFila = 0;
            int sumaColumna = 0;

            for (int j = 0; j < 4; j++) {
                sumaFila += matriz[i][j];
                sumaColumna += matriz[j][i];
            }

            if (sumaFila != sumaBase || sumaColumna != sumaBase) {
                return false;
            }
        }

        return true;
    }

    public void mostrarMatriz(JTextArea resultArea) {
        resultArea.setText("Matriz:\n");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                resultArea.append(matriz[i][j] + " ");
            }
            resultArea.append("\n");
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
        JFrame frame = new JFrame("Ejercicio 5.11");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_11());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_11::mostrarEnVentana);
    }
}
