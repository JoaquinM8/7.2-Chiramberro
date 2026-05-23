package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_5 extends JFrame {

    public ejercicio3_5() {
        setTitle("Ejercicio 3.5");
        setSize(480,222);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel titleLabel = new JLabel("Ingresar Datos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
    
        JTextField minField = new JTextField(5);
        minField.setFont(new Font("Arial", Font.PLAIN, 12));
        minField.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField maxField = new JTextField(5);
        maxField.setFont(new Font("Arial", Font.PLAIN, 12));
        maxField.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton submitButton = new JButton("Ingresar");
      
        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());
                int i = 0;

                if (num >= min & num <= max) {
                    inputField.setEditable(false);
                    minField.setEditable(false);
                    maxField.setEditable(false);
                    submitButton.setEnabled(false);
                    resultArea.append(num + " está dentro del rango");
                } else if (min > max) { 
                    JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    i = 1;
                    while (i == 1) {
                        resultArea.append(num + " no está entre " + minField.getText() + " y " + maxField.getText() + "\n");
                        inputField.setText("");
                        ++i;
                    } 
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;

            JPanel leftPanel = new JPanel(new BorderLayout(5,5));

                JPanel inputPanel = new JPanel(new BorderLayout());

                    JPanel fieldPanel = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc2 = new GridBagConstraints();
                        gbc2.fill = GridBagConstraints.NONE;
                        gbc2.insets = new Insets(5, 5, 5, 5);
                        
                        gbc2.gridx = 0;
                        gbc2.gridy = 0;
                        fieldPanel.add(new JLabel("Número:"), gbc2);

                        gbc2.gridx = 1;
                        gbc2.gridy = 0;
                        fieldPanel.add(inputField, gbc2);

                        gbc2.gridx = 0;
                        gbc2.gridy = 1;
                        fieldPanel.add(new JLabel("Min:"), gbc2);

                        gbc2.gridx = 1;
                        gbc2.gridy = 1;
                        fieldPanel.add(minField, gbc2);

                        gbc2.gridx = 0;
                        gbc2.gridy = 2;
                        fieldPanel.add(new JLabel("Max:"), gbc2);

                        gbc2.gridx = 1;
                        gbc2.gridy = 2;
                        fieldPanel.add(maxField, gbc2);

                    inputPanel.add(fieldPanel, BorderLayout.CENTER);
                    inputPanel.add(submitButton, BorderLayout.SOUTH);
            
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

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ejercicio3_5());
    }
}