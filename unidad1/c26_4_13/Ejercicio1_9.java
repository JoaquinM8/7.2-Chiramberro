package unidad1.c26_4_13;

import javax.swing.*;

public class Ejercicio1_9 extends JFrame {

    public Ejercicio1_9() {
        setTitle("Ejercicio 1.9");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField mantrim1 = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField pertrim1 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JTextField mantrim2 = new JTextField(10);
        JTextField inputField4 = new JTextField(10);
        JTextField pertrim2 = new JTextField(10);
        JTextField inputField5 = new JTextField(10);
        JTextField mantrim3 = new JTextField(10);
        JTextField inputField6 = new JTextField(10);
        JTextField pertrim3 = new JTextField(10);

        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Los beneficios anuales son: ");

        submitButton.addActionListener(e -> {
            try {
                float man1 = Float.parseFloat(inputField.getText());
                float preciomantrim1 = Float.parseFloat(mantrim1.getText());
                float per1 = Float.parseFloat(inputField2.getText());
                float preciopertrim1 = Float.parseFloat(pertrim1.getText());
                float man2 = Float.parseFloat(inputField3.getText());
                float preciomantrim2 = Float.parseFloat(mantrim2.getText());
                float per2 = Float.parseFloat(inputField4.getText());
                float preciopertrim2 = Float.parseFloat(pertrim2.getText());
                float man3 = Float.parseFloat(inputField5.getText());
                float preciomantrim3 = Float.parseFloat(mantrim3.getText());
                float per3 = Float.parseFloat(inputField6.getText());
                float preciopertrim3 = Float.parseFloat(pertrim3.getText());

                float num = (float) ((man1 * preciomantrim1 + per1 * preciopertrim1) + (man2 * preciomantrim2 + per2 * preciopertrim2) + (man3 * preciomantrim3 + per3 * preciopertrim3));

                resultLabel.setText("Los beneficios anuales son: " + num);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en manzanas: "));
        panel.add(inputField);
        panel.add(new JLabel("Precio del kilo: "));
        panel.add(mantrim1);

        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en peras: "));
        panel.add(inputField2);
        panel.add(new JLabel("Precio del kilo: "));
        panel.add(pertrim1);

        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en manzanas: "));
        panel.add(inputField3);
        panel.add(new JLabel("Precio del kilo: "));
        panel.add(mantrim2);  

        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en peras: "));
        panel.add(inputField4);
        panel.add(new JLabel("Precio del kilo: "));
        panel.add(pertrim2);

        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en manzanas: "));
        panel.add(inputField5);
        panel.add(new JLabel("Precio del kilo: "));
        panel.add(mantrim3);

        panel.add(new JLabel("Ingresa los kilos vendidos este trimestre en peras: "));
        panel.add(inputField6);
        panel.add(new JLabel("Precio del kilo: "));
        panel.add(pertrim3);

        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_9 ejercicio = new Ejercicio1_9();
            ejercicio.setVisible(true);
        });
    }
}
