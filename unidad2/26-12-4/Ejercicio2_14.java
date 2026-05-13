import javax.swing.*;

public class Ejercicio2_14 extends JFrame {

    public Ejercicio2_14() {
        setTitle("Ejercicio 2.14");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField diaField = new JTextField(10);
        JTextField mesField = new JTextField(10);
        JTextField añoField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int dia = Integer.parseInt(diaField.getText());
                int mes = Integer.parseInt(mesField.getText());
                int año = Integer.parseInt(añoField.getText());

                
                if (mes == 2 && dia > 28) {
                    resultLabel.setText("Febrero no puede tener más de 28 días.");
                } else if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
                    resultLabel.setText("El mes seleccionado no puede tener más de 30 días.");
                } else if (dia < 1 || dia > 31 || mes < 1 || mes > 12) {
                    resultLabel.setText("Día o mes fuera de rango.");
                } else {
                    if (mes == 12 && dia == 31) {
                        dia = 1;
                        mes = 1;
                        ++año;
                    } else if ((mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10) && dia == 31) {
                        dia = 1;
                        ++mes;
                    } else if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia == 30) {
                        dia = 1;
                        ++mes;
                    } else if (mes == 2 && dia == 28) {
                        dia = 1;
                        ++mes;
                    }
                    else {
                        ++dia;
                    }
                    resultLabel.setText("La fecha un día después es: " + dia + "/" + mes + "/" + año);
                }
                

            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar día:"));
        panel.add(diaField);
        panel.add(new JLabel("Ingresar mes:"));
        panel.add(mesField);
        panel.add(new JLabel("Ingresar año:"));
        panel.add(añoField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_14 ejercicio = new Ejercicio2_14();
            ejercicio.setVisible(true);
        });
    }
}