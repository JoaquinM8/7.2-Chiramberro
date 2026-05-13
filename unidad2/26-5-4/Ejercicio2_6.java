import javax.swing.*;

public class Ejercicio2_6 extends JFrame {

    public Ejercicio2_6() {
        setTitle("Ejercicio 2.6");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try {
                int num = Integer.parseInt(inputField.getText());
                int num2 = Integer.parseInt(inputField2.getText());
                if (num > num2) {
                    resultLabel.setText(num + " -> " + num2 );
                }else if (num < num2){
                    resultLabel.setText(num2 + " -> " + num);
                }else{
                    resultLabel.setText("Iguales");
                };


                
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar número"));
        panel.add(inputField);
        panel.add(inputField2);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_6 ejercicio = new Ejercicio2_6();
            ejercicio.setVisible(true);
        });
    }
}