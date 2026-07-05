package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_2 extends JPanel {

    private int sumEdad = 0;        
    private int countEdad = 0;  
    private int promEdad = 0;  
    private int countMayor = 0;  

    private JLabel totalEdades = new JLabel("Total: ");
    private JLabel sumaEdades = new JLabel("Suma: ");
    private JLabel promedioEdades = new JLabel("Promedio: ");
    private JLabel totalMayores = new JLabel("Adultos: ");

    public ejercicio3_2() {
        
        JLabel titleLabel = new JLabel("Ingresar Edad", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton submitButton = new JButton("Ingresar");
        
        JTextArea edadesArea = new JTextArea(3,20);
        edadesArea.setEditable(false);
        
        totalEdades.setFont(new Font("Arial", Font.BOLD, 15));
        sumaEdades.setFont(new Font("Arial", Font.BOLD, 15));
        promedioEdades.setFont(new Font("Arial", Font.BOLD, 15));
        totalMayores.setFont(new Font("Arial", Font.BOLD, 15));
        

        submitButton.addActionListener(e -> {
            try {
                int edad = Integer.parseInt(inputField.getText());
                int i = 0;

                if (edad < 0) {
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                    totalEdades.setText(totalEdades.getText() + countEdad);
                    sumaEdades.setText(sumaEdades.getText() + sumEdad);
                    promedioEdades.setText(promedioEdades.getText() + promEdad);
                    totalMayores.setText(totalMayores.getText() + countMayor);
                } else {
                    i = 1;
                }

                while (i == 1) {
                    ++countEdad;
                    sumEdad += edad;
                    promEdad = sumEdad / countEdad;
                    edadesArea.append("Edad " + countEdad + ": " + edad + "\n");
                    if (edad >= 18) {
                        ++countMayor;
                    }
                    ++i;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(edadesArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
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

                JPanel resultsPanel = new JPanel(new GridLayout(4,1));

                    resultsPanel.add(totalEdades);
                    resultsPanel.add(sumaEdades);
                    resultsPanel.add(promedioEdades);
                    resultsPanel.add(totalMayores);

                JScrollPane scrollPane = new JScrollPane(
                    edadesArea, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );

                rightPanel.add(scrollPane, BorderLayout.NORTH);
                rightPanel.add(resultsPanel, BorderLayout.CENTER);

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
        JFrame frame = new JFrame("Ejercicio 3.2");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_2());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_2::mostrarEnVentana);
    }
}