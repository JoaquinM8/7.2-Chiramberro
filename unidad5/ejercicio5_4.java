package unidad5;

import javax.swing.*;
import java.awt.*;


// 5.4. Implementar un programa que inicialice una tabla con nuestros números favoritos. A continuación, pedir al usuario el índice de un elemento que será eliminado de la tabla. Continuaremos eliminando elementos hasta que el índice introducido sea negativo o hasta que no existan más elementos que borrar. Es imprescindible controlar que el índice leído corresponde a un dato válido.

public class ejercicio5_4 extends JPanel {

    int numeros[] = {8, 12, 72};
    int cantidad = numeros.length;

    public ejercicio5_4() {

        JLabel titleLabel = new JLabel("Ingresar Indice", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputField = new JTextField(5);
        inputField.setFont(new Font("Arial", Font.PLAIN, 15));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Eliminar");

        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        mostrarTabla(resultArea);

        submitButton.addActionListener(e -> {
            try {
                int indice = Integer.parseInt(inputField.getText());

                if (indice < 0) {
                    inputField.setEditable(false);
                    submitButton.setEnabled(false);
                    resultArea.append("\nPrograma terminado.");
                } else if (indice >= cantidad) {
                    JOptionPane.showMessageDialog(resultArea, "Indice invalido", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    eliminar(indice);
                    mostrarTabla(resultArea);

                    if (cantidad == 0) {
                        inputField.setEditable(false);
                        submitButton.setEnabled(false);
                        resultArea.append("\nNo quedan mas elementos.");
                    }
                }

                inputField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(resultArea, "Error mortal", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;

            JPanel leftPanel = new JPanel(new BorderLayout());

                JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                    inputPanel.add(new JLabel("Indice:"));
                    inputPanel.add(inputField);
                    inputPanel.add(submitButton);

                leftPanel.add(titleLabel, BorderLayout.NORTH);
                leftPanel.add(inputPanel, BorderLayout.CENTER);

            JPanel rightPanel = new JPanel();
                JScrollPane scrollPane = new JScrollPane(
                    resultArea,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );
                rightPanel.add(scrollPane);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 20);
            mainPanel.add(leftPanel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(5, 5, 5, 5);
            mainPanel.add(rightPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public void eliminar(int indice) {
        for (int i = indice; i < cantidad - 1; i++) {
            numeros[i] = numeros[i + 1];
        }
        cantidad--;
    }

    public void mostrarTabla(JTextArea resultArea) {
        resultArea.setText("");
        for (int i = 0; i < cantidad; i++) {
            resultArea.append("Indice " + i + ": " + numeros[i] + "\n");
        }
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 5.4");
        frame.setSize(520, 260);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio5_4());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio5_4::mostrarEnVentana);
    }
}
