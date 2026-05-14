package unidad1.c26_4_13;

import javax.swing.*;

public class Ejercicio1_10 extends JFrame {

    public Ejercicio1_10() {
        setTitle("Ejercicio 1.10");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("La longitud es: ");
        JLabel resultLabel2 = new JLabel("El area de la circunferencia es: ");

        submitButton.addActionListener(e -> {
            try {
                float rad = Float.parseFloat(inputField.getText());
                float longitud = (float) (2 * Math.PI * rad);
                float area = (float) (Math.PI * rad * rad);


                resultLabel.setText("La longitud es: " + longitud);
                resultLabel2.setText("El area de la circunferencia es: " + area);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresa el radio de la circunferencia: "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);
        panel.add(resultLabel2);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_10 ejercicio = new Ejercicio1_10();
            ejercicio.setVisible(true);
        });
    }
}
