package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_13s extends JPanel {
    public e7_13s() {
        PilaTabla pila = new PilaTabla();
        crearPantalla(pila, "PilaTabla");
    }

    private void crearPantalla(PilaTabla pila, String nombre) {
        setLayout(new BorderLayout(10, 10));
        JTextField numero = new JTextField();
        JTextArea resultado = new JTextArea(nombre + "\n", 12, 45);
        resultado.setEditable(false);
        JButton apilar = new JButton("Apilar");
        JButton desapilar = new JButton("Desapilar");
        apilar.addActionListener(e -> {
            try {
                pila.apilar(Integer.parseInt(numero.getText()));
                resultado.append("Se apilo " + numero.getText() + "\n");
            } catch (NumberFormatException error) {
                resultado.append("Escribi un numero entero.\n");
            }
        });
        desapilar.addActionListener(e -> {
            try {
                resultado.append("Sale: " + pila.desapilar() + "\n");
            } catch (IllegalStateException error) {
                resultado.append(error.getMessage() + "\n");
            }
        });
        JPanel acciones = new JPanel();
        acciones.add(new JLabel("Numero:"));
        acciones.add(numero);
        numero.setPreferredSize(new Dimension(100, 25));
        acciones.add(apilar);
        acciones.add(desapilar);
        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(resultado), BorderLayout.CENTER);
    }

    public static void mostrarEnVentana() {
        VentanaEjercicio.mostrar("Ejercicio 7.13 - Pila con tabla", new e7_13s());
    }
}
