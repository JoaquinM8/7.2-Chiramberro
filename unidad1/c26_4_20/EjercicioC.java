package unidad1.c26_4_20;

import javax.swing.*;

public class EjercicioC extends JFrame {

    public EjercicioC() {
        setTitle("Ejercicio C");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Hipotenusa: ");

        submitButton.addActionListener(e -> {
            try {
                int cat1 = Integer.parseInt(inputField.getText());
                int cat2 = Integer.parseInt(inputField2.getText());
                float hip = (float) (Math.hypot(cat1, cat2));
                resultLabel.setText("Hipotenusa: " + hip);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Cateto 1: "));
        panel.add(inputField);
        panel.add(new JLabel("Cateto 2: "));
        panel.add(inputField2);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioC ejercicio = new EjercicioC();
            ejercicio.setVisible(true);
        });
    }
}