package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_14s extends JPanel {
    public e7_14s() {
        setLayout(new BorderLayout(10, 10));
        PilaLista pila = new PilaLista();
        JTextField numero = new JTextField(8);
        JTextArea resultado = new JTextArea("PilaLista\n", 12, 45);
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
        acciones.add(new JLabel("Numero:")); acciones.add(numero); acciones.add(apilar); acciones.add(desapilar);
        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(resultado), BorderLayout.CENTER);
    }

    public static void mostrarEnVentana() {
        VentanaEjercicio.mostrar("Ejercicio 7.14 - Pila con Lista", new e7_14s());
    }
}
