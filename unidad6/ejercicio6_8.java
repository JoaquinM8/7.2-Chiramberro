package unidad6;

import javax.swing.*;
import java.awt.*;

// 6.8. Realizar un programa que lea una frase del teclado y nos indique si es palíndroma, es decir, que la frase sea igual leyendo de izquierda a derecha, que de derecha a izquierda, sin tener en cuenta los espacios. Un ejemplo de frase palíndroma es: Dábale arroz a la zorra el abad. Las vocales con tilde hacen que un algoritmo tome una frase palíndroma como si no lo fuese. Por esto, supondremos que el usuario introduce la frase sin tildes.
public class ejercicio6_8 extends JPanel {

    public ejercicio6_8() {

        JLabel titleLabel = new JLabel("Frase palindroma", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(25);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Comprobar");

        JTextArea resultArea = new JTextArea(8, 20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String frase = inputField.getText().replace(" ", "").toLowerCase();
            String invertida = "";

            for (int i = frase.length() - 1; i >= 0; i--) {
                invertida += frase.charAt(i);
            }

            if (frase.equals(invertida)) {
                resultArea.setText("La frase es palindroma.");
            } else {
                resultArea.setText("La frase no es palindroma.");
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.add(new JScrollPane(resultArea));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 20);
        mainPanel.add(leftPanel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(rightPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 6.8");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_8());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_8::mostrarEnVentana);
    }
}
