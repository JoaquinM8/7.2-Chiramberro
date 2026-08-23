import javax.swing.*;
import java.awt.*;
import unidad7.*;

public class TP7 extends JFrame {
    public TP7() {
        setTitle("TP 7");
        setSize(720, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(menu());
        setVisible(true);
    }

    private JPanel menu() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new GridLayout(2, 3, 10, 10));
        JLabel titulo = new JLabel("TP 7 - PROGRAMAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        agregarBoton(botones, "CuentaCorriente", () -> new e7_1s().setVisible(true));
        agregarBoton(botones, "Texto", e7_6s::mostrarEnVentana);
        agregarBoton(botones, "Banco", e7_7s::mostrarEnVentana);
        agregarBoton(botones, "Sintonizador", e7_8s::mostrarEnVentana);
        agregarBoton(botones, "Bombilla", e7_9s::mostrarEnVentana);
        agregarBoton(botones, "Tren", e7_10s::mostrarEnVentana);
        JPanel menuSuperior = new JPanel(new BorderLayout());
        menuSuperior.add(titulo, BorderLayout.NORTH);
        menuSuperior.add(botones, BorderLayout.CENTER);
        panel.add(menuSuperior, BorderLayout.CENTER);
        return panel;
    }

    private void agregarBoton(JPanel panel, String nombre, Runnable accion) {
        JButton boton = new JButton(nombre);
        boton.addActionListener(e -> accion.run());
        panel.add(boton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TP7::new);
    }
}
