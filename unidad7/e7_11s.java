package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_11s extends JPanel {
    public e7_11s() {
        setLayout(new BorderLayout(10, 10));
        JPanel datos = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField hora = new JTextField("12:30:58");
        JTextField segundos = new JTextField("5");
        datos.add(new JLabel("Hora (HH:MM:SS):"));
        datos.add(hora);
        datos.add(new JLabel("Segundos a avanzar:"));
        datos.add(segundos);

        JTextArea resultado = new JTextArea(12, 45);
        resultado.setEditable(false);
        JButton mostrar = new JButton("Mostrar horas");
        mostrar.addActionListener(e -> {
            try {
                String[] partes = hora.getText().split(":");
                if (partes.length != 3) {
                    throw new IllegalArgumentException("Escribi la hora como HH:MM:SS.");
                }
                Hora reloj = new Hora(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                int cantidad = Integer.parseInt(segundos.getText());
                if (cantidad < 0) {
                    throw new IllegalArgumentException("Los segundos no pueden ser negativos.");
                }
                String texto = reloj + "\n";
                for (int i = 0; i < cantidad; i++) {
                    reloj.avanzarSegundo();
                    texto += reloj + "\n";
                }
                resultado.setText(texto);
            } catch (NumberFormatException error) {
                resultado.setText("La hora y los segundos deben ser numeros.");
            } catch (IllegalArgumentException error) {
                resultado.setText(error.getMessage());
            }
        });
        JPanel acciones = new JPanel();
        acciones.add(mostrar);
        add(datos, BorderLayout.NORTH);
        add(acciones, BorderLayout.CENTER);
        add(new JScrollPane(resultado), BorderLayout.SOUTH);
    }

    public static void mostrarEnVentana() {
        JFrame ventana = new JFrame("Ejercicio 7.11 - Hora");
        ventana.setSize(550, 480);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(new e7_11s());
        ventana.setVisible(true);
    }
}
