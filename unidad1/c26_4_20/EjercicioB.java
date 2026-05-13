package unidad1.c26_4_20;

import javax.swing.*;

public class EjercicioB extends JFrame {

    public EjercicioB() {
        setTitle("Ejercicio B");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Velocidad (m/s): ");

        submitButton.addActionListener(e -> {
            try {
                int kmh = Integer.parseInt(inputField.getText());
                float ms = (float) (kmh * 0.2777777777777777);
                resultLabel.setText("Velocidad (m/s): " + ms);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Velocidad (Km/h): "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioB ejercicio = new EjercicioB();
            ejercicio.setVisible(true);
        });
    }
}