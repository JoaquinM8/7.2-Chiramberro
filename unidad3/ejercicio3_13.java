package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_13 extends JFrame {

    int j = 1;
    int contadorDesastre = 0;
    int contadorCondicioando = 0;
    int contadorBien = 0;
    
    public ejercicio3_13() {
        setTitle("Ejercicio 3.13");
        setSize(480,222);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Ingresar Notas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(5,20);
        resultArea.setEditable(false);

        JLabel aprobadosLabel = new JLabel("Aprobados: ", SwingConstants.CENTER);
        aprobadosLabel.setFont(new Font("Arial", Font.ITALIC, 15));

        JLabel condicionadoLabel = new JLabel("Condicionados: ", SwingConstants.CENTER);
        condicionadoLabel.setFont(new Font("Arial", Font.ITALIC, 15));

        JLabel desastreLabel = new JLabel("Desaprobados: ", SwingConstants.CENTER);
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
                        } else if (num == 4) {
                            contadorCondicioando++;
                        } else {
                            contadorBien++;
                        }
                        ++j;
                        if (j > 6) {
                            submitButton.setEnabled(false);
                            inputField.setEditable(false);
                            aprobadosLabel.setText(aprobadosLabel.getText() + contadorBien);
                            condicionadoLabel.setText(condicionadoLabel.getText() + contadorCondicioando);
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
                
                JPanel evaluacionPanel = new JPanel(new GridLayout(3,1,5,5));
                    evaluacionPanel.add(aprobadosLabel);
                    evaluacionPanel.add(condicionadoLabel);
                    evaluacionPanel.add(desastreLabel);

                rightPanel.add(scrollPane, BorderLayout.NORTH);
                rightPanel.add(evaluacionPanel, BorderLayout.CENTER);
                

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
    SwingUtilities.invokeLater(() -> new ejercicio3_13());
    }
}