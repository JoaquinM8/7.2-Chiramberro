import javax.swing.*;

public class EjercicioA extends JFrame {

    public EjercicioA() {
        setTitle("Ejercicio A");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Grados Fahrenheit: ");

        submitButton.addActionListener(e -> {
            try {
                int cel = Integer.parseInt(inputField.getText());
                float far = (float) (cel * 1.8) + 32;
                resultLabel.setText("Grados Fahrenheit: " + far);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar grados Celsius: "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioA ejercicio = new EjercicioA();
            ejercicio.setVisible(true);
        });
    }
}