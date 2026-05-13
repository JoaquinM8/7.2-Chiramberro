import javax.swing.*;

public class Ejercicio2_11 extends JFrame {

    public Ejercicio2_11() {
        setTitle("Ejercicio 2.11");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int num = Integer.parseInt(inputField.getText());
              
                switch (num){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        resultLabel.setText("Insuficiente");
                        break;
                    case 5:
                        resultLabel.setText("Suficiente");
                        break;
                    case 6:
                        resultLabel.setText("Bien");
                        break;
                    case 7:
                    case 8:
                        resultLabel.setText("Notable");
                        break;
                    case 9:
                    case 10:
                        resultLabel.setText("Sobresaliente");
                        break;
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar nota"));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_11 ejercicio = new Ejercicio2_11();
            ejercicio.setVisible(true);
        });
    }
}