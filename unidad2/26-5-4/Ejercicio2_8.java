import javax.swing.*;

public class Ejercicio2_8 extends JFrame {

    public Ejercicio2_8() {
        setTitle("Ejercicio 2.8");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try {
                int a = Integer.parseInt(inputField.getText());
                int b = Integer.parseInt(inputField2.getText());
                int c = Integer.parseInt(inputField3.getText());

                float pos =(float) ((-b + Math.sqrt( b*b - 4*a*c ))/(2*a));
                float neg =(float) ((-b - Math.sqrt( b*b - 4*a*c))/(2*a));

                if (a==0 | (b*b - 4*a*c ) < 0) {
                    resultLabel.setText("No tiene soluciones reales");
                }

                resultLabel.setText("Raices: " + pos + " " + neg);
                
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
            Ejercicio2_8 ejercicio = new Ejercicio2_8();
            ejercicio.setVisible(true);
        });
    }
}