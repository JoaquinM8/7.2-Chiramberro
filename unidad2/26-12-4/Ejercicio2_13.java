import javax.swing.*;

public class Ejercicio2_13 extends JFrame {

    public Ejercicio2_13() {
        setTitle("Ejercicio 2.13");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField horasField = new JTextField(10);
        JTextField minutosField = new JTextField(10);
        JTextField segundosField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int horas = Integer.parseInt(horasField.getText());
                int minutos = Integer.parseInt(minutosField.getText());
                int segundos = Integer.parseInt(segundosField.getText());

                
                if (segundos == 59) {
                    if (minutos == 59) {
                        if (horas == 23) {
                            horas = 0;
                            minutos = 0;
                        } else {
                            ++horas;
                        }
                        
                    } else {
                        ++minutos;
                    }
                    segundos = 0;
                } else {
                    ++segundos;
                }
                resultLabel.setText("La hora un segundo después es: " + horas + ":" + minutos + ":" + segundos);

            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar horas:"));
        panel.add(horasField);
        panel.add(new JLabel("Ingresar minutos:"));
        panel.add(minutosField);
        panel.add(new JLabel("Ingresar segundos:"));
        panel.add(segundosField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_13 ejercicio = new Ejercicio2_13();
            ejercicio.setVisible(true);
        });
    }
}