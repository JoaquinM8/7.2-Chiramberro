import javax.swing.*;

public class EjercicioF extends JFrame {

    public EjercicioF() {
        setTitle("Ejercicio F");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Cifras del número: ");

        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());

                int c1 = num/100;
                int c2 = num/10%10;
                int c3 = num%10;

                resultLabel.setText("Cifras del número: " + c1 + " / " + c2 + " / " + c3);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingrese un número de 3 cifras: "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioF ejercicio = new EjercicioF();
            ejercicio.setVisible(true);
        });
    }
}