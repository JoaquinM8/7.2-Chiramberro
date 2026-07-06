package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_6 extends JPanel {

    public ejercicio4_6() {
        JLabel titleLabel = new JLabel("VOCAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Verificar");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            String texto = inputField.getText();

            if (texto.length() > 0) {
                char letra = texto.charAt(0);
                if (esVocal(letra)) {
                    resultLabel.setText(letra + " es vocal");
                } else {
                    resultLabel.setText(letra + " no es vocal");
                }
            } else {
                JOptionPane.showMessageDialog(resultLabel, "Ingrese un caracter", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Caracter:"));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static boolean esVocal(char letra) {
        letra = Character.toLowerCase(letra);
        return letra == 'a' || letra == 'e' || letra == 'i' || letra == 'o' || letra == 'u';
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.6");
        frame.setSize(480, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_6());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_6::mostrarEnVentana);
    }
}
