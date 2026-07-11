package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.9. Queremos desarrollar una aplicación que nos ayude a gestionar las notas de un centro educativo. Cada grupo (o clase) está compuesto por 5 alumnos. Se pide leer las notas (números enteros) del primer, segundo y tercer trimestre de un grupo. Debemos mos­trar al final la nota promedio del grupo en cada trimestre, y la nota promedio del alumno que se encuentra en la posición "pos" (que se lee por teclado).

public class ejercicio5_9 extends JPanel {

    int notas[][] = new int[5][3];
    int contador = 0;

    public ejercicio5_9() {

        JLabel titleLabel = new JLabel("Notas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField notaField = new JTextField(5);
        notaField.setFont(new Font("Arial", Font.PLAIN, 15));
        notaField.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField posField = new JTextField(5);
        posField.setFont(new Font("Arial", Font.PLAIN, 15));
        posField.setHorizontalAlignment(SwingConstants.CENTER);
        posField.setEditable(false);

        JButton submitButton = new JButton("Ingresar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        resultArea.setText("Ingrese nota del alumno 1, trimestre 1.\n");

        submitButton.addActionListener(e -> {
            try {
                if (contador < 15) {
                    int nota = Integer.parseInt(notaField.getText());
                    int alumno = contador / 3;
                    int trimestre = contador % 3;
                    notas[alumno][trimestre] = nota;
                    contador++;

                    if (contador == 15) {
                        notaField.setEditable(false);
                        posField.setEditable(true);
                        resultArea.setText("Notas cargadas. Ingrese posicion del alumno (1 a 5).\n");
                    } else {
                        alumno = contador / 3;
                        trimestre = contador % 3;
                        resultArea.append("Ingrese nota del alumno " + (alumno + 1) + ", trimestre " + (trimestre + 1) + ".\n");
                    }
                } else {
                    int pos = Integer.parseInt(posField.getText());
                    if (pos >= 1 && pos <= 5) {
                        mostrarPromedios(resultArea, pos - 1);
                        submitButton.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(resultArea, "Posicion invalida", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                notaField.setText("");
                posField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            JPanel leftPanel = new JPanel(new BorderLayout());
                JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                inputPanel.add(new JLabel("Nota:"));
                inputPanel.add(notaField);
                inputPanel.add(new JLabel("Posicion:"));
                inputPanel.add(posField);
                inputPanel.add(new JLabel(""));
                inputPanel.add(submitButton);
                leftPanel.add(titleLabel, BorderLayout.NORTH);
                leftPanel.add(inputPanel, BorderLayout.CENTER);
            JPanel rightPanel = new JPanel();
                rightPanel.add(new JScrollPane(resultArea));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 20);
            mainPanel.add(leftPanel, gbc);
            gbc.gridx = 1;
            gbc.insets = new Insets(5, 5, 5, 5);
            mainPanel.add(rightPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public void mostrarPromedios(JTextArea resultArea, int pos) {
        resultArea.setText("Promedio por trimestre:\n");

        for (int trimestre = 0; trimestre < 3; trimestre++) {
            double suma = 0;
            for (int alumno = 0; alumno < 5; alumno++) {
                suma += notas[alumno][trimestre];
            }
            resultArea.append("Trimestre " + (trimestre + 1) + ": " + (suma / 5) + "\n");
        }

        double sumaAlumno = 0;
        for (int trimestre = 0; trimestre < 3; trimestre++) {
            sumaAlumno += notas[pos][trimestre];
        }
        resultArea.append("\nPromedio del alumno " + (pos + 1) + ": " + (sumaAlumno / 3));
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 5.9");
        frame.setSize(560, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_9());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_9::mostrarEnVentana);
    }
}
