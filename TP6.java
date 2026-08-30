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
        setJMenuBar(crearBarraMenu());
        add(menu());
        actualizarDatosJugador();
        setVisible(true);
    }

    private String solicitarNombre() {
        String nombre;
        do {
            nombre = JOptionPane.showInputDialog(this, "Ingresa tu nombre:", "JAVA STRING GAMES", JOptionPane.QUESTION_MESSAGE);
            if (nombre == null) {
                return "Jugador";
            }
            nombre = nombre.trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes ingresar un nombre.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            }
        } while (nombre.isEmpty());
        return nombre;
    }

    private void actualizarDatosJugador() {
        datosJugador.setText("Jugador: " + jugador.getNombre() + "    |    Puntaje: " + jugador.getPuntaje());
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu adicionales = new JMenu("Otros ejercicios TP6");
        for (int numero : new int[]{3, 4, 5}) {
            JMenuItem item = new JMenuItem("Ejercicio 6." + numero);
            item.addActionListener(e -> abrirEjercicio(numero, "Ejercicio 6." + numero));
            adicionales.add(item);
        }
        JMenu opciones = new JMenu("Opciones");
        JMenuItem nuevoJugador = new JMenuItem("Nuevo jugador");
        nuevoJugador.addActionListener(e -> {
            jugador.cambiarNombre(solicitarNombre());
            jugador.reiniciarPuntaje();
        });
        JMenuItem ayuda = new JMenuItem("Ayuda");
        ayuda.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Elegí un juego. Los aciertos suman puntos y las respuestas incorrectas los restan.\n"
                + "En el juego final hay tres intentos para adivinar el anagrama.",
            "Ayuda", JOptionPane.INFORMATION_MESSAGE));
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispose());
        opciones.add(nuevoJugador);
        opciones.add(ayuda);
        opciones.addSeparator();
        opciones.add(salir);
        barra.add(adicionales);
        barra.add(opciones);
        return barra;
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

        JLabel lblTitulo = new JLabel("JAVA STRING GAMES", SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 8));
        int ejercicios[] = {1, 2, 6, 7, 8, 9, 12, 13, 14};
        String[] nombres = {"Palabra más corta", "Acierta la contraseña", "El espejo", "Buscador de palabras", "Palíndromo", "Traductor Javalandia", "¿Son anagramas?", "Contador de letras", "Desafío final del anagrama"};
        for (int i = 0; i < ejercicios.length; i++) {
            int ejercicio = ejercicios[i];
            String nombre = nombres[i];
            JButton button = new JButton(nombre);
            button.addActionListener(e -> abrirEjercicio(ejercicio, nombre));
            buttonPanel.add(button);
        }

        JPanel cabecera = new JPanel(new GridLayout(2, 1));
        cabecera.add(lblTitulo);
        datosJugador.setHorizontalAlignment(SwingConstants.CENTER);
        datosJugador.setFont(new Font("Arial", Font.BOLD, 16));
        cabecera.add(datosJugador);
        menuPanel.add(cabecera, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);

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
                return new ejercicio6_1();
            case 2:
                return new ejercicio6_2(jugador);
            case 3:
                return new ejercicio6_3();
            case 4:
                return new ejercicio6_4();
            case 5:
                return new ejercicio6_5();
            case 6:
                return new ejercicio6_6();
            case 7:
                return new ejercicio6_7();
            case 8:
                return new ejercicio6_8();
            case 9:
                return new ejercicio6_9();
            case 12:
                return new ejercicio6_12();
            case 13:
                return new ejercicio6_13();
            case 14:
                return new ejercicio6_14(jugador);
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
        SwingUtilities.invokeLater(TP6::new);
    }
}
