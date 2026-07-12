package unidad6;

import javax.swing.*;
import java.awt.*;

// 6.9. Los habitantes de Javalandia tienen un idioma algo extraño; cuando hablan siempre comienzan sus frases con "Javalín, javalón", para después hacer una pausa más o menos larga y a continuación expresan el mensaje. Existe un dialecto que termina con un silencio y la coletilla "javalén, len, len". Se pide diseñar un traductor que indique si la frase está escrita en el idioma de Javalandia y muestre solo el mensaje sin muletillas.
public class ejercicio6_9 extends JPanel {

    public ejercicio6_9() {

        JLabel titleLabel = new JLabel("Traductor de Javalandia", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(25);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Traducir");

        JTextArea resultArea = new JTextArea(8, 20);
        resultArea.setEditable(false);

        submitButton.addActionListener(e -> {
            String frase = inputField.getText();
            String inicio = "Javalín, javalón";
            String finalFrase = "javalén, len, len";

            if (frase.startsWith(inicio)) {
                resultArea.setText("Mensaje: " + frase.substring(inicio.length()).trim());
            } else if (frase.endsWith(finalFrase)) {
                resultArea.setText("Mensaje: " + frase.substring(0, frase.length() - finalFrase.length()).trim());
            } else {
                resultArea.setText("No es una frase de Javalandia.");
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.add(new JScrollPane(resultArea));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 20);
        mainPanel.add(leftPanel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(rightPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 6.9");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_9());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_9::mostrarEnVentana);
    }
}
