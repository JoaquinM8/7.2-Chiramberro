import javax.swing.*;

public class Ejercicio2_9 extends JFrame {

    public Ejercicio2_9() {
        setTitle("Ejercicio 2.9");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try {
                int num = Integer.parseInt(inputField.getText());

                if (num/10000>1) {
                    resultLabel.setText("5 Cifras");
                }else if (num/1000>1){
                    resultLabel.setText("4 Cifras");
                }else if (num/100>1){
                    resultLabel.setText("3 Cifras");
                }else if (num/10>1){
                    resultLabel.setText("2 Cifras");
                }else{
                    resultLabel.setText("1 Cifra");
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
            Ejercicio2_9 ejercicio = new Ejercicio2_9();
            ejercicio.setVisible(true);
        });
    }
}