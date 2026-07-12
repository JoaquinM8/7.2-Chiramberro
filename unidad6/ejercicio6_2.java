package unidad6;

import javax.swing.*;
import java.awt.*;


// 6.2. Diseñar el juego acierta la contraseña. La mecánica del juego es la siguiente: el pri­ mer jugador introduce la contraseña; a continuación, el segundo jugador debe teclear palabras hasta que la acierte. Realizar dos versiones; en la primera las únicas pistas que se proporcionan son el número de caracteres y cuáles son el primer y el último carácter de la contraseña. En la segunda versión se facilita el juego indicando si la palabra introducida es mayor o menor, alfabéticamente, que la contraseña

public class ejercicio6_2 extends JPanel {

    String contraseña = "";
    int version = 0;

    public ejercicio6_2() {

        JLabel titleLabel = new JLabel("Acierta la contraseña", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(12);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese 1 para la primera version o 2 para la segunda.\n");

        submitButton.addActionListener(e -> {
            String palabra = inputField.getText();

            if (version == 0) {
                try {
                    version = Integer.parseInt(palabra);
                    if (version == 1 || version == 2) {
                        resultArea.setText("Jugador 1, ingrese la contraseña.\n");
                    } else {
                        resultArea.setText("Ingrese 1 o 2.\n");
                        version = 0;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (contraseña.equals("")) {
                contraseña = palabra;

                if (contraseña.equals("")) {
                    resultArea.setText("La contraseña no puede estar vacia.\n");
                } else if (version == 1) {
                    resultArea.setText("Tiene " + contraseña.length() + " caracteres.\n");
                    resultArea.append("Empieza con " + contraseña.charAt(0) + " y termina con " + contraseña.charAt(contraseña.length() - 1) + ".\n");
                } else {
                    resultArea.setText("Jugador 2, intente adivinar la contraseña.\n");
                }
            } else if (palabra.equals(contraseña)) {
                resultArea.append("Adivinaste la contraseña.");
                inputField.setEditable(false);
                submitButton.setEnabled(false);
            } else if (version == 2) {
                if (palabra.compareToIgnoreCase(contraseña) < 0) {
                    resultArea.append("La palabra es menor alfabeticamente.\n");
                } else {
                    resultArea.append("La palabra es mayor alfabeticamente.\n");
                }
            } else {
                resultArea.append("No es la contraseña.\n");
            }

            inputField.setText("");
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
        JFrame frame = new JFrame("Ejercicio 6.2");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_2());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_2::mostrarEnVentana);
    }
}
