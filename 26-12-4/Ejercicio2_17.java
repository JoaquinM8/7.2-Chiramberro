import javax.swing.*;

public class Ejercicio2_17 extends JFrame {

    public Ejercicio2_17() {
        setTitle("Ejercicio 2.17");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField kgcomidaField = new JTextField(10);
        JTextField animalesField = new JTextField(10);
        JTextField kgxanimalesField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int kgcomida = Integer.parseInt(kgcomidaField.getText());
                int animales = Integer.parseInt(animalesField.getText());
                int kgxanimales = Integer.parseInt(kgxanimalesField.getText());

                float racion = (float) kgcomida / animales;
                resultLabel.setText("");

                if (kgcomida < 1 || animales < 1 || kgxanimales < 1) {
                    resultLabel.setText("Introduce números reales.");
                } else {
                    if (kgcomida < animales * kgxanimales) {
                        resultLabel.setText("Cada animal tendra que comer " + racion + "kg de comida.");
                    } else {
                        resultLabel.setText("Cantidad de comida suficiente para alimentar a los animales.");
                    }
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Kg de comida:"));
        panel.add(kgcomidaField);
        panel.add(new JLabel("Número de animales:"));
        panel.add(animalesField);
        panel.add(new JLabel("Kg por animal:"));
        panel.add(kgxanimalesField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_17 ejercicio = new Ejercicio2_17();
            ejercicio.setVisible(true);
        });
    }
}