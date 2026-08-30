package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_3s extends JPanel {
    public e7_3s() {
        setLayout(new FlowLayout());
        JTextArea resultado = new JTextArea(12, 45);
        resultado.setEditable(false);
        JButton probar = new JButton("Probar constructores y metodos");
        probar.addActionListener(e -> {
            CuentaCorriente cuenta1 = new CuentaCorriente("Juan", "12345678");
            CuentaCorriente cuenta2 = new CuentaCorriente(1000.0);
            CuentaCorriente cuenta3 = new CuentaCorriente(500.0, -200.0, "87654321");
            cuenta1.ingreso(300);
            boolean retiro = cuenta1.egreso(100);
            resultado.setText("Cuenta 1\n" + cuenta1.mostrarInformacion()
                    + "\nRetiro realizado: " + retiro
                    + "\n\nCuenta 2\n" + cuenta2.mostrarInformacion()
                    + "\n\nCuenta 3\n" + cuenta3.mostrarInformacion());
        });
        add(probar); add(new JScrollPane(resultado));
    }
}
