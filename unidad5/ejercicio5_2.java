package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.2. Escribir una aplicación que solicite al usuario cuántos números desea introducir. A continuación, se introducirá por teclado esa cantidad de números enteros, y por último, los mostrará en el orden inverso al introducido.
public class ejercicio5_2 extends JPanel {
    
    int i = 0;
    int len = 0;
    int numeros[];
    boolean isCantidad = false;

    public ejercicio5_2() {
        
        JLabel titleLabel = new JLabel("Ingresar Datos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JTextField lenField = new JTextField(5);
        lenField.setFont(new Font("Arial", Font.PLAIN, 12));
        lenField.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField numField = new JTextField(5);
        numField.setFont(new Font("Arial", Font.PLAIN, 15));
        numField.setHorizontalAlignment(SwingConstants.CENTER);
        numField.setEditable(false);
        
        JButton submitButton = new JButton("Ingresar");
      
        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);


        submitButton.addActionListener(e -> {
            try {
                if (isCantidad == false) {
                    len = Integer.parseInt(lenField.getText());
                    if (len > 0) {
                        numField.setEditable(true);
                        lenField.setEditable(false);
                        isCantidad = true;
                        numeros = new int[len];
                    } else {
                        JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    int num = Integer.parseInt(numField.getText());
                    if (i<len) {
                        numeros[i] = num;
                        numField.setText("");
                        i++;
                        if (i == len) {
                            numField.setEditable(false);
                            submitButton.setEnabled(false);
                            for (int j = len - 1; j >= 0; j--) {
                                resultArea.append("Número " + (j + 1) + ": " + numeros[j] + "\n");
                            }
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

            JPanel leftPanel = new JPanel(new BorderLayout(5,5));

                JPanel inputPanel = new JPanel(new BorderLayout());

                    JPanel fieldPanel = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc2 = new GridBagConstraints();
                        gbc2.fill = GridBagConstraints.NONE;
                        gbc2.insets = new Insets(5, 5, 5, 5);
                        
                        gbc2.gridx = 0;
                        gbc2.gridy = 0;
                        fieldPanel.add(new JLabel("Cantidad:"), gbc2);

                        gbc2.gridx = 1;
                        gbc2.gridy = 0;
                        fieldPanel.add(lenField, gbc2);

                        gbc2.gridx = 0;
                        gbc2.gridy = 1;
                        fieldPanel.add(new JLabel("Número:"), gbc2);

                        gbc2.gridx = 1;
                        gbc2.gridy = 1;
                        fieldPanel.add(numField, gbc2);

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
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 5.2");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_2());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_2::mostrarEnVentana);
    }
}