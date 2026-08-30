package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_12s extends JPanel {
    public e7_12s() {
        setLayout(new BorderLayout(10, 10));
        Lista lista = new Lista();
        JTextField numero = new JTextField();
        JTextField indice = new JTextField();
        JTextArea resultado = new JTextArea(10, 45);
        resultado.setEditable(false);

        JPanel datos = new JPanel(new GridLayout(2, 2, 5, 5));
        datos.add(new JLabel("Numero:"));
        datos.add(numero);
        datos.add(new JLabel("Indice:"));
        datos.add(indice);

        JPanel acciones = new JPanel(new GridLayout(2, 3, 5, 5));
        agregarBoton(acciones, "Agregar final", () -> lista.insertarFinal(leerNumero(numero)), resultado, lista);
        agregarBoton(acciones, "Agregar principio", () -> lista.insertarPrincipio(leerNumero(numero)), resultado, lista);
        agregarBoton(acciones, "Insertar indice", () -> lista.insertar(leerNumero(numero), leerNumero(indice)), resultado, lista);
        agregarBoton(acciones, "Eliminar indice", () -> lista.eliminar(leerNumero(indice)), resultado, lista);
        agregarBoton(acciones, "Buscar", () -> resultado.setText("Indice encontrado: " + lista.buscar(leerNumero(numero))), resultado, lista);
        agregarBoton(acciones, "Cantidad", () -> resultado.setText("Cantidad: " + lista.numeroElementos()), resultado, lista);
        add(datos, BorderLayout.NORTH);
        add(acciones, BorderLayout.CENTER);
        add(new JScrollPane(resultado), BorderLayout.SOUTH);
    }

    private int leerNumero(JTextField campo) {
        return Integer.parseInt(campo.getText());
    }

    private void agregarBoton(JPanel panel, String texto, Runnable accion, JTextArea resultado, Lista lista) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> {
            try {
                accion.run();
                resultado.append("\nLista: " + lista);
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(this, "Escribi un numero entero.");
            } catch (IllegalArgumentException error) {
                JOptionPane.showMessageDialog(this, error.getMessage());
            }
        });
        panel.add(boton);
    }

    public static void mostrarEnVentana() {
        VentanaEjercicio.mostrar("Ejercicio 7.12 - Lista", new e7_12s());
    }
}
