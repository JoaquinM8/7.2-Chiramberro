import javax.swing.*;

public class Ejercicio2_10 extends JFrame {

    public Ejercicio2_10() {
        setTitle("Ejercicio 2.10");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int num = Integer.parseInt(inputField.getText());
                int c1 = num/1000;
                int c2 = num/100%10;
                int c3 = num/10%10;
                int c4 = num%10;
                String cap = "";
                String cs1 = String.valueOf(c1);
                String cs2 = String.valueOf(c2);
                String cs3 = String.valueOf(c3);
                String cs4 = String.valueOf(c4);
                if (num/1000>1) {
                    cap = cs4+cs3+cs2+cs1;
                }
                else if (num/100>1) {
                    cap = cs4+cs3+cs2;
                }
                else if (num/10>1) {
                    cap = cs4+cs3;
                };
                
                int capnum = Integer.parseInt(cap);

                if (capnum == num | num/10<1) {
                    resultLabel.setText("Capicua");
                }else{
                    resultLabel.setText("No!"); 
                }


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
            Ejercicio2_10 ejercicio = new Ejercicio2_10();
            ejercicio.setVisible(true);
        });
    }
}