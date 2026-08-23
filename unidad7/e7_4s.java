package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_4s extends JPanel {
    public e7_4s() {
        setLayout(new FlowLayout());
        JTextField nombre = new JTextField(15);
        JTextArea resultado = new JTextArea(7, 35);
        resultado.setEditable(false);
        JButton probar = new JButton("Modificar nombre publico");
        probar.addActionListener(e -> {
            CuentaCorriente cuenta = new CuentaCorriente("Titular original", "12345678");
            cuenta.nombre = nombre.getText();
            resultado.setText("Nombre publico: " + cuenta.nombre
                    + "\nSaldo y limite son privados."
                    + "\nDNI tiene visibilidad de paquete.");
        });
        add(new JLabel("Nuevo nombre:")); add(nombre); add(probar); add(new JScrollPane(resultado));
    }
}
