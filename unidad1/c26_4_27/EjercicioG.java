package unidad1.c26_4_27;

import javax.swing.*;

public class EjercicioG extends JFrame {

    public EjercicioG() {
        setTitle("Ejercicio G");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel c1Label = new JLabel("");
        JLabel c12Label = new JLabel("");
        JLabel c123Label = new JLabel("");
        JLabel c1234Label = new JLabel("");
        JLabel c12345Label = new JLabel("");
        submitButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(inputField.getText());

                int c1 = num/10000;
                int c2 = num/1000%10;
                int c3 = num/100%10;
                int c4 = num/10%10;

                c1Label.setText(""+c1);
                c12Label.setText(""+c1+c2);
                c123Label.setText(""+c1+c2+c3);
                c1234Label.setText(""+c1+c2+c3+c4);
                c12345Label.setText(""+num);
            } catch (NumberFormatException ex) {
                c1Label.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingrese un número de 5 cifras: "));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(c1Label);
        panel.add(c12Label);
        panel.add(c123Label);
        panel.add(c1234Label);
        panel.add(c12345Label);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EjercicioG ejercicio = new EjercicioG();
            ejercicio.setVisible(true);
        });
    }
}