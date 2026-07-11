package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.6 Diseñar una aplicación para gestionar un campeonato de programación, donde se introducen la puntuación (enteros) obtenidos por 5 programadores, conforme van terminando su prueba. La aplicación debe mostrar las puntuaciones ordenadas de los 5 participantes. En ocasiones, cuando finalizan los 5 participantes anteriores, se suman al campeonato un máximo de 3 programadores de exhibición, cuyos puntos se incluyen con el resto. La forma de especificar que no intervienen más programadores de exhibición es introducir como puntuación un -1(menos 1) La aplicación debe mostrar, finalmente, los puntos ordenados de todos los participantes.

public class ejercicio5_6 extends JPanel {

    int puntos[] = new int[8];
    int cantidad = 0;

    public ejercicio5_6() {

        JLabel titleLabel = new JLabel("Campeonato", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese puntos del programador 1.\n");

        submitButton.addActionListener(e -> {
            try {
                int punto = Integer.parseInt(inputField.getText());

                if (cantidad < 5) {
                    puntos[cantidad] = punto;
                    cantidad++;

                    if (cantidad == 5) {
                        mostrarOrdenados(resultArea);
                        resultArea.append("\nIngrese exhibicion o -1 para terminar.\n");
                    } else {
                        resultArea.append("Ingrese puntos del programador " + (cantidad + 1) + ".\n");
                    }
                } else {
                    if (punto == -1 || cantidad == 8) {
                        mostrarOrdenados(resultArea);
                        inputField.setEditable(false);
                        submitButton.setEnabled(false);
                    } else {
                        puntos[cantidad] = punto;
                        cantidad++;
                        mostrarOrdenados(resultArea);

                        if (cantidad == 8) {
                            inputField.setEditable(false);
                            submitButton.setEnabled(false);
                        }
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

    public void mostrarOrdenados(JTextArea resultArea) {
        int copia[] = new int[cantidad];

        for (int i = 0; i < cantidad; i++) {
            copia[i] = puntos[i];
        }

        ordenar(copia);
        resultArea.setText("Puntuaciones ordenadas:\n");
        for (int i = 0; i < copia.length; i++) {
            resultArea.append(copia[i] + "\n");
        }
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
        JFrame frame = new JFrame("Ejercicio 5.6");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_6());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_6::mostrarEnVentana);
    }
}
