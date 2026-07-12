package unidad6;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

// 6.14. Implementar el juego del anagrama, que consiste en que un jugador escribe una palabra o frase, y la aplicación muestra un anagrama del texto introducido generado al azar. A continuación otro jugador tiene que acertar cuál es el texto original. La aplicación no debe permitir que el texto introducido por el jugador 1 sea la cadena vacía.
public class ejercicio6_14 extends JPanel {

    String original = "";
    Random random = new Random();

    public ejercicio6_14() {

        JLabel titleLabel = new JLabel("Juego del anagrama", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(18);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        resultArea.setText("Jugador 1, ingrese una palabra o frase.\n");

        submitButton.addActionListener(e -> {
            if (original.equals("")) {
                original = inputField.getText();

                if (original.equals("")) {
                    resultArea.setText("No puede ingresar una cadena vacia.\n");
                } else {
                    String anagrama = mezclar(original);
                    resultArea.setText("Jugador 2, adivine el texto.\nAnagrama: " + anagrama + "\n");
                }
            } else if (inputField.getText().equals(original)) {
                resultArea.append("Adivinaste el texto.");
                inputField.setEditable(false);
                submitButton.setEnabled(false);
            } else {
                resultArea.append("No es el texto original.\n");
            }

            inputField.setText("");
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

    public String mezclar(String texto) {
        char letras[] = texto.toCharArray();

        for (int i = 0; i < letras.length; i++) {
            int pos = random.nextInt(letras.length);
            char aux = letras[i];
            letras[i] = letras[pos];
            letras[pos] = aux;
        }

        return new String(letras);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 6.14");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio6_14());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio6_14::mostrarEnVentana);
    }
}
