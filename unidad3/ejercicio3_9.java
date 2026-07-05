package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_9 extends JPanel {

    int alturamax = 0;
    int arbolmax = 0;
    int narbol = 0;

    public ejercicio3_9() {

        JLabel titleLabel = new JLabel("Ingresar Altura", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea arbolsArea = new JTextArea(5,20);
        arbolsArea.setEditable(false);

        JLabel resultadoAltura = new JLabel("", SwingConstants.CENTER);
        resultadoAltura.setFont(new Font("Arial", Font.BOLD, 12));
        
        submitButton.addActionListener(e -> {
            try {
                int altura = Integer.parseInt(inputField.getText());
                int j = 1;
                inputField.setText("");
                if (altura < 0) {
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                    resultadoAltura.setText("Altura máxima: Arbol " + arbolmax + " " + alturamax + "cm");
                } else {
                    while (j == 1) {
                        arbolsArea.append("Arbol " + narbol + ": " + altura + "\n");
                        if (altura > alturamax) {
                            alturamax = altura;
                            arbolmax = narbol;
                        }
                        ++narbol;
                        ++j;
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(arbolsArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
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
            
            JPanel rightPanel = new JPanel(new BorderLayout(10,10));

                JScrollPane scrollPane = new JScrollPane(
                    arbolsArea, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );

                rightPanel.add(scrollPane, BorderLayout.NORTH);
                rightPanel.add(resultadoAltura, BorderLayout.CENTER);

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
        JFrame frame = new JFrame("Ejercicio 3.9");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_9());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_9::mostrarEnVentana);
    }
}