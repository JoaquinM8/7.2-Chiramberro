import javax.swing.*;

public class Ejercicio2_15 extends JFrame {

    public Ejercicio2_15() {
        setTitle("Ejercicio 2.15");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int dia = Integer.parseInt(inputField.getText());
                
                switch (dia) {
                    case 1: resultLabel.setText("Lunes"); break;
                    case 2: resultLabel.setText("Martes"); break;
                    case 3: resultLabel.setText("Miércoles"); break;
                    case 4: resultLabel.setText("Jueves"); break;
                    case 5: resultLabel.setText("Viernes"); break;
                    case 6: resultLabel.setText("Sábado"); break;
                    case 7: resultLabel.setText("Domingo"); break;
                    default: resultLabel.setText("Número fuera de rango. Introduce un número entre 1 y 7.");
                }
                
                

            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar día:"));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_15 ejercicio = new Ejercicio2_15();
            ejercicio.setVisible(true);
        });
    }
}