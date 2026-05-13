package unidad1.c26_4_6;

import javax.swing.*;

public class Ejercicio1_3 extends JFrame {

    public Ejercicio1_3() {
        setTitle("Ejercicio 1.2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("La edad actual es: ");

        submitButton.addActionListener(e -> {
            try {
                int año = Integer.parseInt(inputField.getText());
                int nac = Integer.parseInt(inputField2.getText());
                int edad = año-nac;
                resultLabel.setText("Tu edad es: " + edad);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Año actual: "));
        panel.add(inputField);
        panel.add(new JLabel("Año de nacimiento: "));
        panel.add(inputField2);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_3 ejercicio = new Ejercicio1_3();
            ejercicio.setVisible(true);
        });
    }
}