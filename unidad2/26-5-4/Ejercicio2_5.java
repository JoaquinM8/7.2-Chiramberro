import javax.swing.*;

public class Ejercicio2_5 extends JFrame {

    public Ejercicio2_5() {
        setTitle("Ejercicio 2.5");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
            try {
                float num = Float.parseFloat(inputField.getText());
                if (num > -1 & num < 1) {
                    resultLabel.setText(num + " Es casi cero");
                }else{
                     resultLabel.setText(" No");
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
            Ejercicio2_5 ejercicio = new Ejercicio2_5();
            ejercicio.setVisible(true);
        });
    }
}