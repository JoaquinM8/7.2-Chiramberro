package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_8s extends JPanel {
    public static void mostrarEnVentana() {
        JFrame ventana = new JFrame("Sintonizador");
        ventana.setSize(420, 180);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(new e7_8s());
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(e7_8s::mostrarEnVentana);
    }

    public e7_8s() {
        setLayout(new FlowLayout());
        Sintonizador sintonizador = new Sintonizador();
        JLabel frecuencia = new JLabel(sintonizador.display() + " MHz");
        frecuencia.setFont(new Font("Arial", Font.BOLD, 30));
        JButton bajar = new JButton("Bajar");
        JButton subir = new JButton("Subir");
        bajar.addActionListener(e -> { sintonizador.down(); frecuencia.setText(sintonizador.display() + " MHz"); });
        subir.addActionListener(e -> { sintonizador.up(); frecuencia.setText(sintonizador.display() + " MHz"); });
        add(bajar); add(frecuencia); add(subir);
    }
}
