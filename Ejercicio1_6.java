
import javax.swing.*;

public class Ejercicio1_6 extends JFrame {

    public Ejercicio1_6() {
        setTitle("Ejercicio 1.6");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("´Promedio de notas: ");

        submitButton.addActionListener(e -> {
            try {
                float nota1 = Float.parseFloat(inputField.getText());
                float nota2 =  Float.parseFloat(inputField2.getText());
                float nota3 =  Float.parseFloat(inputField3.getText());
                float prom = (float) (nota1+nota2+nota3)/3;
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
        panel.add(new JLabel("Nota 3: "));
        panel.add(inputField3);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio1_6 ejercicio = new Ejercicio1_6();
            ejercicio.setVisible(true);
        });
    }
}