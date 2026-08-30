package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_18s extends JPanel {
    public e7_18s() {
        setLayout(new BorderLayout(10, 10));
        JTextField primero = new JTextField("1,2,3");
        JTextField segundo = new JTextField("2,3,4");
        JTextArea resultado = new JTextArea(12, 45);
        resultado.setEditable(false);
        JPanel datos = new JPanel(new GridLayout(2, 2, 5, 5));
        datos.add(new JLabel("Conjunto 1 (separado por comas):")); datos.add(primero);
        datos.add(new JLabel("Conjunto 2 (separado por comas):")); datos.add(segundo);
        JButton calcular = new JButton("Calcular operaciones");
        calcular.addActionListener(e -> {
            try {
                Conjunto c1 = crearConjunto(primero.getText());
                Conjunto c2 = crearConjunto(segundo.getText());
                resultado.setText("C1: " + c1 + "\nC2: " + c2
                        + "\nIncluido C1 en C2: " + Conjunto.incluido(c1, c2)
                        + "\nUnion: " + Conjunto.union(c1, c2)
                        + "\nInterseccion: " + Conjunto.interseccion(c1, c2)
                        + "\nDiferencia C1 - C2: " + Conjunto.diferencia(c1, c2));
            } catch (NumberFormatException error) {
                resultado.setText("Usa numeros enteros separados por comas.");
            }
        });
        JPanel acciones = new JPanel();
        acciones.add(calcular);
        add(datos, BorderLayout.NORTH);
        add(acciones, BorderLayout.CENTER);
        add(new JScrollPane(resultado), BorderLayout.SOUTH);
    }

    private Conjunto crearConjunto(String texto) {
        Conjunto conjunto = new Conjunto();
        String[] numeros = texto.split(",");
        for (int i = 0; i < numeros.length; i++) {
            conjunto.insertar(Integer.parseInt(numeros[i].trim()));
        }
        return conjunto;
    }

    public static void mostrarEnVentana() {
        VentanaEjercicio.mostrar("Ejercicio 7.18 - Operaciones con conjuntos", new e7_18s());
    }
}
