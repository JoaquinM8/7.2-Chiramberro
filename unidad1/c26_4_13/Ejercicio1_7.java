package unidad1.c26_4_13;

import javax.swing.*;

public class Ejercicio1_7 extends JFrame {

    public Ejercicio1_7() {
        setTitle("Ejercicio 1.7");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Redondeado: ");

        submitButton.addActionListener(e -> {
            try {
                float num = Float.parseFloat(inputField.getText());
                int redondeado = Math.round(num);
                resultLabel.setText("Redondeado: " + redondeado);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresa el numero: "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_7 ejercicio = new Ejercicio1_7();
            ejercicio.setVisible(true);
        });
    }
}
