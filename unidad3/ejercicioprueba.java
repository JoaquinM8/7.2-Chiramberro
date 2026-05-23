package unidad3;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ejercicioprueba extends JFrame {

    private JTextField inputField;
    private JTextArea outputArea;

    public ejercicioprueba() {
        setTitle("Ejercicio 3_1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inputField = new JTextField(10);
        JButton submitButton = new JButton("Introduzca un número");
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int num = Integer.parseInt(inputField.getText());
                    while (num != 0) {
                        boolean esPar = num % 2 == 0;
                        boolean esPositivo = num >= 0;
                       
                        outputArea.append("Número: " + num + "\n");
                        outputArea.append("Par: " + esPar + "\n");
                        outputArea.append("Positivo: " + esPositivo + "\n");
                        outputArea.append("Cuadrado: " + num * num + "\n\n");

                        inputField.setText("");
                        break;
                    }
                } catch (NumberFormatException ex) {
                    outputArea.append("Error: Introduce un número válido\n\n");
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Número:"));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(inputPanel);
        mainPanel.add(scrollPane);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ejercicioprueba ejercicio = new ejercicioprueba();
            ejercicio.setVisible(true);
        });
    }
}