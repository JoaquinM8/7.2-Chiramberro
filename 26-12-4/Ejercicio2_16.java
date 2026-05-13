import javax.swing.*;

public class Ejercicio2_16 extends JFrame {

    public Ejercicio2_16() {
        setTitle("Ejercicio 2.16");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Enviar");
        JLabel resultLabel = new JLabel("");
        submitButton.addActionListener(e -> {
             try { 
                int num = Integer.parseInt(inputField.getText());
                
                if (num < 1 || num > 99) {
                    resultLabel.setText("Número fuera de rango. Introduce un número entre 1 y 99.");
                } else if (num > 10 && num < 20) {
                    switch (num) {
                        case 11: resultLabel.setText("Once"); break;
                        case 12: resultLabel.setText("Doce"); break;
                        case 13: resultLabel.setText("Trece"); break;
                        case 14: resultLabel.setText("Catorce"); break;
                        case 15: resultLabel.setText("Quince"); break;
                        case 16: resultLabel.setText("Dieciséis"); break;
                        case 17: resultLabel.setText("Diecisiete"); break;
                        case 18: resultLabel.setText("Dieciocho"); break;
                        case 19: resultLabel.setText("Diecinueve"); break;
                    }

                } else if (num > 20 && num < 30) {
                    switch (num) {
                        case 21: resultLabel.setText("Veintiuno"); break;
                        case 22: resultLabel.setText("Veintidos"); break;
                        case 23: resultLabel.setText("Veintitres"); break;
                        case 24: resultLabel.setText("Veinticuatro"); break;
                        case 25: resultLabel.setText("Veinticinco"); break;
                        case 26: resultLabel.setText("Veintiséis"); break;
                        case 27: resultLabel.setText("Veintisiete"); break;
                        case 28: resultLabel.setText("Veintiocho"); break;
                        case 29: resultLabel.setText("Veintinueve"); break;
                    }
                } else {
                    int decenas = num / 10;
                    int unidades = num % 10;
                    String resultado = "";

                    switch (decenas) {
                        case 1: resultado += "Diez"; break;
                        case 2: resultado += "Veinte"; break;
                        case 3: resultado += "Treinta"; break;
                        case 4: resultado += "Cuarenta"; break;
                        case 5: resultado += "Cincuenta"; break;
                        case 6: resultado += "Sesenta"; break;
                        case 7: resultado += "Setenta"; break;
                        case 8: resultado += "Ochenta"; break;
                        case 9: resultado += "Noventa"; break;
                    }
                    
                    if (unidades > 0) {
                        if (resultado != "") {
                            resultado += " y ";
                        }
                        switch (unidades) {
                            case 1: resultado += "Uno"; break;
                            case 2: resultado += "Dos"; break;
                            case 3: resultado += "Tres"; break;
                            case 4: resultado += "Cuatro"; break;
                            case 5: resultado += "Cinco"; break;
                            case 6: resultado += "Seis"; break;
                            case 7: resultado += "Siete"; break;
                            case 8: resultado += "Ocho"; break;
                            case 9: resultado += "Nueve"; break;
                        }
                    }
                    
                    resultLabel.setText("" + resultado);
                }
                
                

            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresar número:"));
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(resultLabel);

        add(panel);
        setSize(600, 100);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ejercicio2_16 ejercicio = new Ejercicio2_16();
            ejercicio.setVisible(true);
        });
    }
}