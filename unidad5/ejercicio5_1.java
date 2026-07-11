package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.1. Diseñar un programa que solicite al usuario que introduzca por teclado 5 números decimales a continuación, debe mostrar los números en el mismo orden que se han introducido.

public class ejercicio5_1 extends JPanel {


    public ejercicio5_1() {

        JLabel titleLabel = new JLabel("Ingresar Número", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);

        double numeros[] = new double[5];

        submitButton.addActionListener(e -> {
            try {
                
                double num = Double.parseDouble(inputField.getText());
                for (int i = 0; i < numeros.length; i++) {
                    if (numeros[i] == 0) {
                        numeros[i] = num;
                        break;
                    }
                }
                resultArea.setText("");
                for (int i = 0; i < numeros.length; i++) {
                    resultArea.append("Número " + (i + 1) + ": " + numeros[i] + "\n");
                }
                
                if (numeros[numeros.length - 1] != 0) {
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                }
                
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

                JScrollPane scrollPane = new JScrollPane(
                    resultArea, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );

                rightPanel.add(scrollPane);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 20);
            mainPanel.add(leftPanel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(5, 5, 5, 5);
            mainPanel.add(rightPanel, gbc);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }



    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 5.1");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_1());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_1::mostrarEnVentana);
    }
}
