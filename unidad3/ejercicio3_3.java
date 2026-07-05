package unidad3;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ejercicio3_3 extends JPanel {

    private Random random = new Random();

    private int randomNum = random.nextInt(1,100);
    private int atts = 1;
    private JLabel winLabel = new JLabel("", SwingConstants.CENTER);

    public ejercicio3_3() {
        
        JLabel titleLabel = new JLabel("Ingresar Número", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton submitButton = new JButton("Ingresar");
        
        JTextArea intentosArea = new JTextArea(3,20);
        intentosArea.setEditable(false);
        
        winLabel.setFont(new Font("Arial", Font.BOLD, 15));

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                int i = 0;

                if (num < 0) {
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                    winLabel.setText("El número era: " + randomNum);
                } else if (num == randomNum) {
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                    winLabel.setText("¡Adivinado en " + atts + " intentos!");
                } else {
                    i = 1;
                }

                while (i == 1) {
                    if (num > randomNum) {
                        if (num > randomNum + 10) {
                            intentosArea.append(num + " ⭣⭣" + "\n");
                        } else {
                            intentosArea.append(num + " ⭣" + "\n");
                        }
                    } else {
                        if (num < randomNum - 10) {
                            intentosArea.append(num + " ⭡⭡" + "\n");
                        } else {
                            intentosArea.append(num + " ⭡" + "\n");
                        }
                    }
                    ++atts;
                    ++i;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(intentosArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;

            JPanel leftPanel = new JPanel(new BorderLayout(5,5));

                JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                
                    inputPanel.add(inputField);
                    inputPanel.add(submitButton);
            
                leftPanel.add(titleLabel, BorderLayout.NORTH);
                leftPanel.add(inputPanel, BorderLayout.CENTER);
            
            JPanel rightPanel = new JPanel(new BorderLayout(5,5));

                JScrollPane scrollPane = new JScrollPane(
                    intentosArea, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );

                rightPanel.add(scrollPane, BorderLayout.NORTH);
                rightPanel.add(winLabel, BorderLayout.CENTER);

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
        JFrame frame = new JFrame("Ejercicio 3.3");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_3());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_3::mostrarEnVentana);
    }
}