import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import unidad3.*;

public class TP3 extends JFrame {

    private CardLayout navegador = new CardLayout();
    private JPanel contenedor = new JPanel(navegador);

    public TP3() {
        setTitle("TP 3");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(menu());
        setVisible(true);
    }

    private Border crearBordeDefault(Integer top, Integer left, Integer bottom, Integer right) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(top, left, bottom, right),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
            )
        );
    }

    private JPanel menu() {
        JPanel panel = new JPanel(new BorderLayout());

        contenedor.add(inicio(), "Inicio");

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("TP 3 - PROGRAMAS", SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 5, 10, 5));
        for (int i = 1; i <= 15; i++) {
            int ejercicio = i;
            String nombre = "Ejercicio 3." + i;
            JButton button = new JButton(nombre);
            button.addActionListener(e -> {
                setTitle("TP 3 - " + nombre);
                reiniciarEjercicio(nombre, crearEjercicio(ejercicio));
                navegador.show(contenedor, nombre);
            });
            buttonPanel.add(button);
        }

        menuPanel.add(lblTitulo, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(menuPanel, BorderLayout.NORTH);
        panel.add(contenedor, BorderLayout.CENTER);

        navegador.show(contenedor, "Inicio");
        return panel;
    }

    private void reiniciarEjercicio(String nombre, JPanel ejercicio) {
        for (Component component : contenedor.getComponents()) {
            if (nombre.equals(component.getName())) {
                contenedor.remove(component);
                break;
            }
        }

        ejercicio.setName(nombre);
        contenedor.add(ejercicio, nombre);
        contenedor.revalidate();
        contenedor.repaint();
    }

    private JPanel crearEjercicio(int ejercicio) {
        switch (ejercicio) {
            case 1:
                return new ejercicio3_1();
            case 2:
                return new ejercicio3_2();
            case 3:
                return new ejercicio3_3();
            case 4:
                return new ejercicio3_4();
            case 5:
                return new ejercicio3_5();
            case 6:
                return new ejercicio3_6();
            case 7:
                return new ejercicio3_7();
            case 8:
                return new ejercicio3_8();
            case 9:
                return new ejercicio3_9();
            case 10:
                return new ejercicio3_10();
            case 11:
                return new ejercicio3_11();
            case 12:
                return new ejercicio3_12();
            case 13:
                return new ejercicio3_13();
            case 14:
                return new ejercicio3_14();
            case 15:
                return new ejercicio3_15();
            default:
                return inicio();
        }
    }

    private JPanel inicio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Seleccione un programa", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 45));

        JPanel wordPanel = new JPanel(new BorderLayout());
        wordPanel.setBackground(Color.GRAY);
        wordPanel.add(lblTitulo, BorderLayout.CENTER);

        panel.add(wordPanel, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TP3::new);
    }
}
