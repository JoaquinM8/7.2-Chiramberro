package unidad1.c26_4_13;

import javax.swing.*;

public class Ejercicio1_8 extends JFrame {

    public Ejercicio1_8() {
        setTitle("Ejercicio 1.7");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JTextField inputField4 = new JTextField(10);
        JTextField inputField5 = new JTextField(10);
        JTextField inputField6 = new JTextField(10);

        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Los beneficios anuales son: ");

        submitButton.addActionListener(e -> {
            try {
                float man1 = Float.parseFloat(inputField.getText());
                float per1 = Float.parseFloat(inputField2.getText());
                float man2 = Float.parseFloat(inputField3.getText());
                float per2 = Float.parseFloat(inputField4.getText());
                float man3 = Float.parseFloat(inputField5.getText());
                float per3 = Float.parseFloat(inputField6.getText());

                float num = (float) ((man1 * 2.35 + per1 * 1.95) + (man2 * 2.35 + per2 * 1.95) + (man3 * 2.35 + per3 * 1.95));

                resultLabel.setText("Los beneficios anuales son: " + num);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en manzanas: "));
        panel.add(inputField);
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en peras: "));
        panel.add(inputField2);
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en manzanas: "));
        panel.add(inputField3);
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en peras: "));
        panel.add(inputField4);
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en manzanas: "));
        panel.add(inputField5);
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en peras: "));
        panel.add(inputField6);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_8 ejercicio = new Ejercicio1_8();
            ejercicio.setVisible(true);
        });
    }
}
