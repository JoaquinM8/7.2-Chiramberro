package unidad1.c26_4_6;

import javax.swing.*;

public class Ejercicio1_4 extends JFrame {

    public Ejercicio1_4() {
        setTitle("Ejercicio 1.4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("El siguiente numero es: ");
        JLabel resultLabel2JLabel = new JLabel("El anterior numero es: ");

        submitButton.addActionListener(e -> {
            try {
                short num = Short.parseShort(inputField.getText());
                short sig = (short) (num + 1);
                short ant = (short) (num - 1);
                resultLabel.setText("El siguiente numero es:" + sig);
                resultLabel2JLabel.setText("El numero anterior es:" + ant);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("El numero es: "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);
        panel.add(resultLabel2JLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_4 ejercicio = new Ejercicio1_4();
            ejercicio.setVisible(true);
        });
    }
}