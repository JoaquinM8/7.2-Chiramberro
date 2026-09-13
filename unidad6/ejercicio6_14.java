package unidad6;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ejercicio6_14 extends JPanel {
    private static final int MAX_INTENTOS = 3;
    private final Random random = new Random();
    private final Jugador jugador;
    private String original = "";
    private int intentos;
    private final JTextField entrada = new JTextField(20);
    private final JTextArea resultado = new JTextArea(9, 28);
    private final JButton enviar = new JButton("Comenzar partida");
    private final JButton nuevaPartida = new JButton("Nueva partida");
    private final JButton abandonar = new JButton("Abandonar");

    public ejercicio6_14() { this(null); }

    public ejercicio6_14(Jugador jugador) {
        this.jugador = jugador;
        setLayout(new BorderLayout(8, 8));
        JLabel titulo = new JLabel("Desafío final del anagrama", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);
        JPanel controles = new JPanel(new FlowLayout());
        controles.add(new JLabel("Texto / respuesta:"));
        controles.add(entrada);
        controles.add(enviar);
        controles.add(nuevaPartida);
        controles.add(abandonar);
        nuevaPartida.setEnabled(false);
        resultado.setEditable(false);
        resultado.setText("Jugador 1: ingresá una palabra o frase para comenzar.\n");
        enviar.addActionListener(e -> procesarEntrada());
        nuevaPartida.addActionListener(e -> reiniciar());
        abandonar.addActionListener(e -> abandonarJuego());
        add(controles, BorderLayout.CENTER);
        add(new JScrollPane(resultado), BorderLayout.SOUTH);
    }

    private void procesarEntrada() {
        String texto = entrada.getText();
        if (texto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar una palabra o frase.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        entrada.setText("");
        if (original.isEmpty()) {
            original = texto;
            intentos = MAX_INTENTOS;
            resultado.setText("Jugador 2: adiviná el texto original.\nAnagrama: " + crearAnagrama(original) + "\nIntentos restantes: " + intentos + "\n");
            enviar.setText("Probar respuesta");
            return;
        }
        if (texto.equals(original)) {
            resultado.append("¡Correcto! Descubriste el texto original. +100 puntos.\n");
            if (jugador != null) jugador.desafioDificilCorrecto();
            terminar();
            return;
        }
        intentos--;
        if (jugador != null) jugador.respuestaIncorrecta();
        if (intentos == 0) {
            resultado.append("Respuesta incorrecta. -10 puntos. No quedan intentos. El texto era: " + original + ".\n");
            terminar();
        } else {
            resultado.append("Respuesta incorrecta. -10 puntos. Intentos restantes: " + intentos + ".\n");
        }
    }

    private void abandonarJuego() {
        if (jugador != null) jugador.abandonarJuego();
        resultado.append("Juego abandonado. El puntaje no cambia.\n");
        terminar();
    }

    private void terminar() {
        entrada.setEnabled(false);
        enviar.setEnabled(false);
        nuevaPartida.setEnabled(true);
        abandonar.setEnabled(false);
    }

    private void reiniciar() {
        original = "";
        intentos = 0;
        entrada.setText("");
        entrada.setEnabled(true);
        enviar.setEnabled(true);
        enviar.setText("Comenzar partida");
        nuevaPartida.setEnabled(false);
        abandonar.setEnabled(true);
        resultado.setText("Jugador 1: ingresá una palabra o frase para comenzar.\n");
    }

    private String crearAnagrama(String texto) {
        String anagrama = mezclar(texto);
        for (int i = 0; i < 10 && anagrama.equals(texto); i++) anagrama = mezclar(texto);
        return anagrama;
    }

    public String mezclar(String texto) {
        char[] letras = texto.toCharArray();
        for (int i = letras.length - 1; i > 0; i--) {
            int posicion = random.nextInt(i + 1);
            char auxiliar = letras[i];
            letras[i] = letras[posicion];
            letras[posicion] = auxiliar;
        }
        return new String(letras);
    }
}
