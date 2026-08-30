package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_17s extends JPanel {
    public e7_17s() {
        setLayout(new BorderLayout(10, 10));
        Conjunto conjunto = new Conjunto();
        JTextField numero = new JTextField(8);
        JTextArea resultado = new JTextArea(12, 45);
        resultado.setEditable(false);
        JButton insertar = new JButton("Insertar");
        JButton eliminar = new JButton("Eliminar");
        insertar.addActionListener(e -> cambiarConjunto(conjunto, numero, resultado, true));
        eliminar.addActionListener(e -> cambiarConjunto(conjunto, numero, resultado, false));
        JPanel acciones = new JPanel();
        acciones.add(new JLabel("Numero:")); acciones.add(numero); acciones.add(insertar); acciones.add(eliminar);
        add(acciones, BorderLayout.NORTH);
        add(new JScrollPane(resultado), BorderLayout.CENTER);
    }

    private void cambiarConjunto(Conjunto conjunto, JTextField campo, JTextArea resultado, boolean insertar) {
        try {
            int numero = Integer.parseInt(campo.getText());
            if (insertar) {
                conjunto.insertar(numero);
            } else {
                conjunto.eliminar(numero);
            }
            resultado.setText("Conjunto: " + conjunto + "\nCantidad: " + conjunto.numeroElementos());
        } catch (NumberFormatException error) {
            resultado.setText("Escribi un numero entero.");
        }
    }

    public static void mostrarEnVentana() {
        VentanaEjercicio.mostrar("Ejercicio 7.17 - Conjunto", new e7_17s());
    }
}
