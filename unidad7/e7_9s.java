package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_9s extends JPanel {
    public static void mostrarEnVentana() {
        JFrame ventana = new JFrame("Bombilla");
        ventana.setSize(650, 220);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(new e7_9s());
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(e7_9s::mostrarEnVentana);
    }

    public e7_9s() {
        setLayout(new FlowLayout());
        Bombilla bombilla1 = new Bombilla();
        Bombilla bombilla2 = new Bombilla();
        Bombilla bombilla3 = new Bombilla();
        JLabel estado1 = new JLabel("Bombilla 1 apagada");
        JLabel estado2 = new JLabel("Bombilla 2 apagada");
        JLabel estado3 = new JLabel("Bombilla 3 apagada");
        JButton encender = new JButton("Encender 1");
        JButton apagar = new JButton("Apagar 1");
        JButton encender2 = new JButton("Encender 2");
        JButton apagar2 = new JButton("Apagar 2");
        JButton encender3 = new JButton("Encender 3");
        JButton apagar3 = new JButton("Apagar 3");
        JButton cortar = new JButton("Cortar luz general");
        JButton reparar = new JButton("Reparar fusible");
        encender.addActionListener(e -> { bombilla1.encender(); actualizar(estado1, bombilla1, 1); });
        apagar.addActionListener(e -> { bombilla1.apagar(); actualizar(estado1, bombilla1, 1); });
        encender2.addActionListener(e -> { bombilla2.encender(); actualizar(estado2, bombilla2, 2); });
        apagar2.addActionListener(e -> { bombilla2.apagar(); actualizar(estado2, bombilla2, 2); });
        encender3.addActionListener(e -> { bombilla3.encender(); actualizar(estado3, bombilla3, 3); });
        apagar3.addActionListener(e -> { bombilla3.apagar(); actualizar(estado3, bombilla3, 3); });
        cortar.addActionListener(e -> {
            Bombilla.cortarLuz();
            actualizar(estado1, bombilla1, 1); actualizar(estado2, bombilla2, 2); actualizar(estado3, bombilla3, 3);
        });
        reparar.addActionListener(e -> {
            Bombilla.repararFusible();
            actualizar(estado1, bombilla1, 1); actualizar(estado2, bombilla2, 2); actualizar(estado3, bombilla3, 3);
        });
        add(encender); add(apagar); add(encender2); add(apagar2); add(encender3); add(apagar3);
        add(cortar); add(reparar); add(estado1); add(estado2); add(estado3);
    }
    private void actualizar(JLabel estado, Bombilla bombilla, int numero) {
        estado.setText(bombilla.mostrarEstado() ? "Bombilla " + numero + " encendida" : "Bombilla " + numero + " apagada");
    }
}
