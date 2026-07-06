package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_10 extends JPanel {

    public ejercicio4_10() {
        JLabel titleLabel = new JLabel("NUMEROS AMIGOS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField1 = new JTextField(5);
        JTextField inputField2 = new JTextField(5);
        inputField1.setHorizontalAlignment(SwingConstants.CENTER);
        inputField2.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Verificar");

        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        submitButton.addActionListener(e -> {
            try {
                int a = Integer.parseInt(inputField1.getText());
                int b = Integer.parseInt(inputField2.getText());

                if (sonAmigos(a, b)) {
                    resultLabel.setText(a + " y " + b + " son amigos");
                } else {
                    resultLabel.setText(a + " y " + b + " no son amigos");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultLabel, "Ingrese numeros enteros", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Numero 1:"));
        inputPanel.add(inputField1);
        inputPanel.add(new JLabel("Numero 2:"));
        inputPanel.add(inputField2);
        inputPanel.add(submitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static boolean sonAmigos(int a, int b) {
        return sumaDivisores(a) == b && sumaDivisores(b) == a;
    }

    public static int sumaDivisores(int n) {
        int suma = 0;

        for (int i = 1; i < n; i++) {
            if (n % i == 0) {
                suma += i;
            }
        }

        return suma;
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.10");
        frame.setSize(560, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_10());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_10::mostrarEnVentana);
    }
}
