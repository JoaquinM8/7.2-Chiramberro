package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_15s extends JPanel {
    public e7_15s() {
        setLayout(new BorderLayout(10, 10));
        ColaTabla cola = new ColaTabla();
        JTextField numero = new JTextField(8);
        JTextArea resultado = new JTextArea("ColaTabla\n", 12, 45);
        resultado.setEditable(false);
        JButton encolar = new JButton("Encolar");
        JButton desencolar = new JButton("Desencolar");
        encolar.addActionListener(e -> {
            try {
                cola.encolar(Integer.parseInt(numero.getText()));
                resultado.append("Entra " + numero.getText() + "\n");
            } catch (NumberFormatException error) {
                resultado.append("Escribi un numero entero.\n");
            }
        });
        desencolar.addActionListener(e -> {
            try {
                resultado.append("Sale: " + cola.desencolar() + "\n");
            } catch (IllegalStateException error) {
                resultado.append(error.getMessage() + "\n");
            }
        });
        JPanel acciones = new JPanel();
        acciones.add(new JLabel("Numero:")); acciones.add(numero); acciones.add(encolar); acciones.add(desencolar);
        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(resultado), BorderLayout.CENTER);
    }

    public static void mostrarEnVentana() {
        VentanaEjercicio.mostrar("Ejercicio 7.15 - Cola con tabla", new e7_15s());
    }
}
