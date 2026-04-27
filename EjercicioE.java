import javax.swing.*;

public class EjercicioE extends JFrame {

    public EjercicioE() {
        setTitle("Ejercicio E");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField ladoAField = new JTextField(10);
        JTextField ladoBField = new JTextField(10);
        JTextField ladoCField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Área del triángulo: ");

        submitButton.addActionListener(e -> {
            try {
                float ladoA = Float.parseFloat(ladoAField.getText());
                float ladoB = Float.parseFloat(ladoBField.getText());
                float ladoC = Float.parseFloat(ladoCField.getText());

                float p = (ladoA + ladoB + ladoC) / 2;
                float area = (float) Math.sqrt(p*(p-ladoA)*(p-ladoC)*(p-ladoC));

                resultLabel.setText("Área del triángulo: " + area);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Lado A: "));
        panel.add(ladoAField);
        panel.add(new JLabel("Lado B: "));
        panel.add(ladoBField);
        panel.add(new JLabel("Lado C: "));
        panel.add(ladoCField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioE ejercicio = new EjercicioE();
            ejercicio.setVisible(true);
        });
    }
}