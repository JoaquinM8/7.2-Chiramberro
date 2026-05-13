import javax.swing.*;

public class EjercicioD extends JFrame {

    public EjercicioD() {
        setTitle("Ejercicio D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Volumen: ");

        submitButton.addActionListener(e -> {
            try {
                int rad = Integer.parseInt(inputField.getText());
                float vol = (float) ((4.0/3.0) * Math.PI * Math.pow(rad, 3));
                resultLabel.setText("Volumen: " + vol);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Radio de la esfera (cm): "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioD ejercicio = new EjercicioD();
            ejercicio.setVisible(true);
        });
    }
}