package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_12 extends JPanel {

    int j = 1;
    int contadorDesastre = 0;
    
    public ejercicio3_12() {

        JLabel titleLabel = new JLabel("Ingresar Notas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(5,20);
        resultArea.setEditable(false);

        JLabel desastreLabel = new JLabel("Desaprobados < 4: ", SwingConstants.CENTER);
        desastreLabel.setFont(new Font("Arial", Font.ITALIC, 15));



        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                int i = 1;
                inputField.setText("");
                if (num < 1 || num > 10) {
                    JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    while (i == 1) {
                        resultArea.append("Nota " + j + ": " + num + "\n");
                        ++i;
                        if (num < 4) {
                            contadorDesastre++;
                        }
                        ++j;
                        if (j > 5) {
                            submitButton.setEnabled(false);
                            inputField.setEditable(false);
                            desastreLabel.setText(desastreLabel.getText() + contadorDesastre);
                        }
                    }
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
            
            JPanel rightPanel = new JPanel(new BorderLayout(5,5));

                JScrollPane scrollPane = new JScrollPane(
                    resultArea, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );

                rightPanel.add(scrollPane, BorderLayout.NORTH);
                rightPanel.add(desastreLabel, BorderLayout.CENTER);

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
        JFrame frame = new JFrame("Ejercicio 3.12");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_12());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_12::mostrarEnVentana);
    }
}