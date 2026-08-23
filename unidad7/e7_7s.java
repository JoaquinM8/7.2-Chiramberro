package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_7s extends JPanel {
    public static void mostrarEnVentana() {
        JFrame ventana = new JFrame("Banco");
        ventana.setSize(520, 300);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(new e7_7s());
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(e7_7s::mostrarEnVentana);
    }

    public e7_7s() {
        setLayout(new FlowLayout());
        JTextField nombreBanco = new JTextField(12);
        JTextField capital = new JTextField(10);
        JTextField direccion = new JTextField(15);
        JTextArea resultado = new JTextArea(8, 35);
        resultado.setEditable(false);
        JButton crear = new JButton("Crear y vincular");
        crear.addActionListener(e -> {
            Banco banco;
            try {
                if (capital.getText().trim().isEmpty()) {
                    banco = new Banco(nombreBanco.getText());
                } else {
                    banco = new Banco(nombreBanco.getText(), Double.parseDouble(capital.getText()), direccion.getText());
                }
            } catch (NumberFormatException error) {
                resultado.setText("El capital debe ser un numero.");
                return;
            }
            banco.cambiarDireccionCentral(direccion.getText());
            CuentaCorriente cuenta = new CuentaCorriente("Titular", "12345678");
            boolean sinBanco = cuenta.obtenerBanco() == null;
            cuenta.asignarBanco(banco);
            CuentaCorriente.cambiarNombreBanco(nombreBanco.getText());
            resultado.setText("Cuenta sin banco inicialmente: " + sinBanco
                    + "\nBanco vinculado: " + banco.mostrarInformacion()
                    + "\n\n" + cuenta.mostrarInformacion());
        });
        add(new JLabel("Banco:")); add(nombreBanco);
        add(new JLabel("Capital:")); add(capital);
        add(new JLabel("Direccion:")); add(direccion); add(crear);
        add(new JScrollPane(resultado));
    }
}
