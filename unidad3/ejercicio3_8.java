package unidad3;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

public class ejercicio3_8 extends JPanel {

    public ejercicio3_8() {

        JLabel titleLabel = new JLabel("Ingresar Número", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(4,20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                BigInteger producto = BigInteger.ONE;
                if (num < 1) {
                    JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
                }
                 else {
                    for (int i = num ; i >= 1 ; i--) {
                        if (i == 1) {
                            resultArea.append(""+i);
                        } else {
                            resultArea.append(i + " x ");
                        }
                        producto = producto.multiply(BigInteger.valueOf(i));
                    }
                    resultArea.append("\n" + "Factorial: " + producto);
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
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
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
        JFrame frame = new JFrame("Ejercicio 3.8");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_8());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_8::mostrarEnVentana);
    }
}