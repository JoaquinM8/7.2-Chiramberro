package unidad1.c26_4_27;

import javax.swing.*;

public class EjercicioH extends JFrame {

    public EjercicioH() {
        setTitle("Ejercicio H");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField diaField = new JTextField(10);
        JTextField mesField = new JTextField(10);
        JTextField añoField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("Número de la suerte: ");
        submitButton.addActionListener(e -> {
            try {
                int d = Integer.parseInt(diaField.getText());
                int m = Integer.parseInt(mesField.getText());
                int a = Integer.parseInt(añoField.getText());

                int num = d+m+a;
                int luckynumber = num/1000 + num/100%10 + num/10%10 + num%10;
                resultLabel.setText("Número de la suerte: "+luckynumber);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar fecha de nacimiento"));
        
        panel.add(new JLabel("Día"));
        panel.add(diaField);
        
        panel.add(new JLabel("Mes"));
        panel.add(mesField);
        
        panel.add(new JLabel("Año"));
        panel.add(añoField);

        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioH ejercicio = new EjercicioH();
            ejercicio.setVisible(true);
        });
    }
}