package unidad6;

import javax.swing.*;
import java.awt.*;


// 6.3. Introducir por teclado una frase palabra a palabra, y mostrar la frase completa se­ parando las palabras introducidas con espacios en blanco. Terminar de leer la frase cuando alguna de las palabras introducidas sea la cadena «fin» escrita con cualquier combinación de mayúsculas/minúsculas. La cadena «fin» no aparecerá en la frase final.

public class ejercicio6_3 extends JPanel {

    String frase = "";

    public ejercicio6_3() {

        JLabel titleLabel = new JLabel("Formar frase", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(10);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese palabras. Escriba fin para terminar.\n");

        submitButton.addActionListener(e -> {
            String palabra = inputField.getText();

            if (palabra.equalsIgnoreCase("fin")) {
                resultArea.setText("Frase: " + frase);
                inputField.setEditable(false);
                submitButton.setEnabled(false);
            } else {
                frase += palabra + " ";
                resultArea.append(palabra + "\n");
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
        JFrame frame = new JFrame("Ejercicio 6.3");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_3());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_3::mostrarEnVentana);
    }
}
