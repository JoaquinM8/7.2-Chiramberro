package unidad4;

import javax.swing.*;
import java.awt.*;

public class ejercicio4_2 extends JPanel {

    public ejercicio4_2() {
        JLabel titleLabel = new JLabel("NUMEROS COMPRENDIDOS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField1 = new JTextField(5);
        JTextField inputField2 = new JTextField(5);
        inputField1.setHorizontalAlignment(SwingConstants.CENTER);
        inputField2.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Mostrar");

        JTextArea resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            try {
                int a = Integer.parseInt(inputField1.getText());
                int b = Integer.parseInt(inputField2.getText());
                resultArea.setText(numerosEntre(a, b));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultArea, "Ingrese numeros enteros", "Error", JOptionPane.ERROR_MESSAGE);
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
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static String numerosEntre(int a, int b) {
        String texto = "";

        if (a <= b) {
            for (int i = a; i <= b; i++) {
                texto = texto + i + "\n";
            }
        } else {
            for (int i = a; i >= b; i--) {
                texto = texto + i + "\n";
            }
        }

        return texto;
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 4.2");
        frame.setSize(520, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio4_2());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio4_2::mostrarEnVentana);
    }
}
