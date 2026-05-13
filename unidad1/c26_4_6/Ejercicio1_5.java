package unidad1.c26_4_6;

import javax.swing.*;

public class Ejercicio1_5 extends JFrame {

    public Ejercicio1_5() {
        setTitle("Ejercicio 1.4");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("´Promedio de notas: ");

        submitButton.addActionListener(e -> {
            try {
                int nota1 = Integer.parseInt(inputField.getText());
                int nota2 = Integer.parseInt(inputField2.getText());
                float prom = (float) (nota1+nota2)/2;
                resultLabel.setText("Promedio de notas:" + prom);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Nota 1: "));
        panel.add(inputField);
        panel.add(new JLabel("Nota 2: "));
        panel.add(inputField2);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_5 ejercicio = new Ejercicio1_5();
            ejercicio.setVisible(true);
        });
    }
}