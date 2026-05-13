import javax.swing.*;

public class Ejercicio2_1 extends JFrame {

    public Ejercicio2_1() {
        setTitle("Ejercicio 2.1");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());
                if (num%2 == 0) {
                    resultLabel.setText("Par");
                }else{
                    resultLabel.setText("Impar");
                };


                
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar número"));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_1 ejercicio = new Ejercicio2_1();
            ejercicio.setVisible(true);
        });
    }
}