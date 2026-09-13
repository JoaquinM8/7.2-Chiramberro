import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import unidad6.*;

public class TP6 extends JFrame {
    private final CardLayout navegador = new CardLayout();
    private final JPanel contenedor = new JPanel(navegador);
    private final Jugador jugador;
    private final JLabel datosJugador = new JLabel();

    public TP6() {
        jugador = new Jugador(solicitarNombre());
        jugador.agregarObservador(this::actualizarDatosJugador);
        setTitle("JAVA STRING GAMES");
        setSize(820, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(menu());
        actualizarDatosJugador();
        setVisible(true);
    }

    private String solicitarNombre() {
        String nombre;
        do {
            nombre = JOptionPane.showInputDialog(this, "Ingresa tu nombre:", "JAVA STRING GAMES", JOptionPane.QUESTION_MESSAGE);
            if (nombre == null) return "Jugador";
            nombre = nombre.trim();
            if (nombre.isEmpty()) JOptionPane.showMessageDialog(this, "Debes ingresar un nombre.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
        } while (nombre.isEmpty());
        return nombre;
    }

    private void actualizarDatosJugador() {
        datosJugador.setText("Jugador: " + jugador.getNombre() + "    |    Puntaje: " + jugador.getPuntaje());
    }

    private Border crearBordeDefault(int arriba, int izquierda, int abajo, int derecha) {
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(arriba, izquierda, abajo, derecha), BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(arriba, izquierda, abajo, derecha)));
    }

    private JPanel menu() {
        JPanel panel = new JPanel(new BorderLayout());
        contenedor.add(inicio(), "Inicio");
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel titulo = new JLabel("JAVA STRING GAMES", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        datosJugador.setHorizontalAlignment(SwingConstants.CENTER);
        datosJugador.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel cabecera = new JPanel(new GridLayout(2, 1));
        cabecera.add(titulo);
        cabecera.add(datosJugador);

        JPanel juegos = new JPanel(new GridLayout(3, 3, 10, 8));
        int[] ejercicios = {1, 2, 6, 7, 8, 9, 12, 13, 14};
        String[] nombres = {"Palabra más corta", "Acierta la contraseña", "El espejo", "Buscador de palabras", "Palíndromo", "Traductor Javalandia", "¿Son anagramas?", "Contador de letras", "Desafío final del anagrama"};
        for (int i = 0; i < ejercicios.length; i++) {
            int ejercicio = ejercicios[i];
            String nombre = nombres[i];
            JButton boton = new JButton(nombre);
            boton.addActionListener(e -> abrirEjercicio(ejercicio, nombre));
            juegos.add(boton);
        }

        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton nuevoJugador = new JButton("Nuevo jugador");
        nuevoJugador.addActionListener(e -> { jugador.cambiarNombre(solicitarNombre()); jugador.reiniciarPuntaje(); });
        JButton ayuda = new JButton("Ayuda");
        ayuda.addActionListener(e -> JOptionPane.showMessageDialog(this, "Contraseña: acertar suma 50 puntos; un error resta 10.\nDesafío final: acertar suma 100 puntos; un error resta 10.\nAbandonar no cambia el puntaje.", "Ayuda", JOptionPane.INFORMATION_MESSAGE));
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> dispose());
        opciones.add(nuevoJugador);
        opciones.add(ayuda);
        opciones.add(salir);

        menuPanel.add(cabecera, BorderLayout.NORTH);
        menuPanel.add(juegos, BorderLayout.CENTER);
        menuPanel.add(opciones, BorderLayout.SOUTH);
        panel.add(menuPanel, BorderLayout.NORTH);
        panel.add(contenedor, BorderLayout.CENTER);
        navegador.show(contenedor, "Inicio");
        return panel;
    }

    private void abrirEjercicio(int ejercicio, String nombre) {
        setTitle("JAVA STRING GAMES - " + nombre);
        reiniciarEjercicio(nombre, crearEjercicio(ejercicio));
        navegador.show(contenedor, nombre);
    }

    private void reiniciarEjercicio(String nombre, JPanel ejercicio) {
        for (Component componente : contenedor.getComponents()) {
            if (nombre.equals(componente.getName())) { contenedor.remove(componente); break; }
        }
        ejercicio.setName(nombre);
        contenedor.add(ejercicio, nombre);
        contenedor.revalidate();
        contenedor.repaint();
    }

    private JPanel crearEjercicio(int ejercicio) {
        switch (ejercicio) {
            case 1: return new ejercicio6_1();
            case 2: return new ejercicio6_2(jugador);
            case 6: return new ejercicio6_6();
            case 7: return new ejercicio6_7();
            case 8: return new ejercicio6_8();
            case 9: return new ejercicio6_9();
            case 12: return new ejercicio6_12();
            case 13: return new ejercicio6_13();
            case 14: return new ejercicio6_14(jugador);
            default: return inicio();
        }
    }

    private JPanel inicio() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel mensaje = new JLabel("Seleccioná un juego", SwingConstants.CENTER);
        mensaje.setFont(new Font("Arial", Font.BOLD, 35));
        panel.add(mensaje, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(TP6::new); }
}
