package unidad5;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


// 5.5. Desarrollar el juego la cámara secreta, que consiste abrir una cámara mediante su combinación secreta, que está formado por una combinación de dígitos del uno al cinco. El jugador especificará cuál es la longitud de la combinación, a mayor longitud mayor será la dificultad del juego. La aplicación genera, de forma aleatoria, una combinación secreta que el usuario tendrá que acertar. En cada intento se muestra como pista, para cada dígito de la combinación introducido por el jugador, si es mayor, menor o igual que el correspondiente en la combinación secreta.

public class ejercicio5_5 extends JPanel {

    int secreto[];
    boolean jugando = false;
    Random random = new Random();

    public ejercicio5_5() {

        JLabel titleLabel = new JLabel("Camara Secreta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(8);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Primero ingrese la longitud de la combinacion.\n");

        submitButton.addActionListener(e -> {
            try {
                if (jugando == false) {
                    int longitud = Integer.parseInt(inputField.getText());

                    if (longitud <= 0) {
                        JOptionPane.showMessageDialog(resultArea, "Longitud invalida", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        secreto = new int[longitud];
                        for (int i = 0; i < secreto.length; i++) {
                            secreto[i] = random.nextInt(5) + 1;
                        }
                        jugando = true;
                        resultArea.setText("Combinacion generada. Ingrese " + longitud + " digitos del 1 al 5.\n");
                    }
                } else {
                    String intento = inputField.getText();

                    if (intento.length() != secreto.length) {
                        JOptionPane.showMessageDialog(resultArea, "Debe ingresar " + secreto.length + " digitos", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        boolean gano = true;
                        resultArea.append("\nIntento: " + intento + "\n");

                        for (int i = 0; i < secreto.length; i++) {
                            int digito = Character.getNumericValue(intento.charAt(i));

                            if (digito < 1 || digito > 5) {
                                JOptionPane.showMessageDialog(resultArea, "Solo se permiten digitos del 1 al 5", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            if (digito < secreto[i]) {
                                resultArea.append("Posicion " + (i + 1) + ": mayor\n");
                                gano = false;
                            } else if (digito > secreto[i]) {
                                resultArea.append("Posicion " + (i + 1) + ": menor\n");
                                gano = false;
                            } else {
                                resultArea.append("Posicion " + (i + 1) + ": igual\n");
                            }
                        }

                        if (gano) {
                            resultArea.append("\nAbriste la camara secreta.");
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

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;

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
        JFrame frame = new JFrame("Ejercicio 5.5");
        frame.setSize(560, 320);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_5());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_5::mostrarEnVentana);
    }
}
