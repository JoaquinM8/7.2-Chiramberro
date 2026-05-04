import javax.swing.*;

public class Ejercicio2_7 extends JFrame {

    public Ejercicio2_7() {
        setTitle("Ejercicio 2.7");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try {
                int num = Integer.parseInt(inputField.getText());
                int num2 = Integer.parseInt(inputField2.getText());
                int num3 = Integer.parseInt(inputField3.getText());
                if (num > num2) {
                    if (num2 > num3) {
                        resultLabel.setText(num + " " + num2 + " " + num3 );
                    } else if (num > num3) {
                        resultLabel.setText(num + " " + num3 + " " + num2 );
                    } else {
                        resultLabel.setText(num3 + " " + num + " " + num2 );
                    }  
                }else if (num2 > num){
                    if (num > num3) {
                        resultLabel.setText(num2 + " " + num + " " + num3 );
                    } else if (num2 > num3) {
                        resultLabel.setText(num2 + " " + num3 + " " + num2 );
                    } else {
                        resultLabel.setText(num3 + " " + num2 + " " + num );
                    }
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
        panel.add(inputField3);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_7 ejercicio = new Ejercicio2_7();
            ejercicio.setVisible(true);
        });
    }
}