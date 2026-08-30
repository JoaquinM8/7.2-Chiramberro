package unidad6;

import javax.swing.*;
import java.awt.*;

public class ejercicio6_2 extends JPanel {
    private String contrasena = "";
    private int version;
    private final Jugador jugador;
    private final JTextField intentoField = new JTextField(14);
    private final JPasswordField contrasenaField = new JPasswordField(14);
    private final JTextArea resultado = new JTextArea(9, 25);
    private final JButton continuar = new JButton("Continuar");

    public ejercicio6_2() { this(null); }

    public ejercicio6_2(Jugador jugador) {
        this.jugador = jugador;
        setLayout(new BorderLayout(8, 8));
        JLabel titulo = new JLabel("Acierta la contraseña", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);
        resultado.setEditable(false);
        resultado.setText("Elegí una versión: 1 (pistas) o 2 (pistas alfabéticas).\n");
        JPanel entrada = new JPanel(new GridLayout(0, 2, 6, 6));
        entrada.add(new JLabel("Versión (1 o 2):"));
        JTextField versionField = new JTextField(14);
        entrada.add(versionField);
        entrada.add(new JLabel("Jugador 1 - contraseña:")); entrada.add(contrasenaField);
        entrada.add(new JLabel("Jugador 2 - intento:")); entrada.add(intentoField);
        entrada.add(new JLabel()); entrada.add(continuar);
        contrasenaField.setEnabled(false); intentoField.setEnabled(false);
        continuar.addActionListener(e -> procesar(versionField));
        add(entrada, BorderLayout.CENTER);
        add(new JScrollPane(resultado), BorderLayout.SOUTH);
    }

    private void procesar(JTextField versionField) {
        if (version == 0) {
            String textoVersion = versionField.getText().trim();
            if (!textoVersion.equals("1") && !textoVersion.equals("2")) { advertir("Ingresá 1 o 2 para elegir la versión."); return; }
            version = Integer.parseInt(textoVersion);
            versionField.setEnabled(false); contrasenaField.setEnabled(true);
            resultado.setText("Jugador 1: ingresá la contraseña. Quedará oculta.\n"); return;
        }
        if (contrasena.isEmpty()) {
            String ingresada = new String(contrasenaField.getPassword());
            if (ingresada.trim().isEmpty()) { advertir("La contraseña no puede estar vacía."); return; }
            contrasena = ingresada; contrasenaField.setText(""); contrasenaField.setEnabled(false); intentoField.setEnabled(true);
            resultado.setText("Jugador 2: adiviná la contraseña. Tiene " + contrasena.length() + " caracteres, comienza con '" + contrasena.charAt(0) + "' y termina con '" + contrasena.charAt(contrasena.length() - 1) + "'.\n"); return;
        }
        String intento = intentoField.getText();
        if (intento.trim().isEmpty()) { advertir("Debes ingresar un intento."); return; }
        intentoField.setText("");
        if (intento.equals(contrasena)) {
            resultado.append("¡Adivinaste la contraseña! +50 puntos.\n"); cambiarPuntaje(50); intentoField.setEnabled(false); continuar.setEnabled(false);
        } else {
            resultado.append("Contraseña incorrecta. -10 puntos.\n"); cambiarPuntaje(-10);
            if (version == 2) resultado.append("La palabra introducida es alfabéticamente " + (intento.compareToIgnoreCase(contrasena) < 0 ? "menor" : "mayor") + " que la contraseña.\n");
        }
    }

    private void cambiarPuntaje(int puntos) { if (jugador != null) { if (puntos >= 0) jugador.sumarPuntos(puntos); else jugador.restarPuntos(-puntos); } }
    private void advertir(String mensaje) { JOptionPane.showMessageDialog(this, mensaje, "Dato inválido", JOptionPane.WARNING_MESSAGE); }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> { JFrame ventana = new JFrame("Ejercicio 6.2"); ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); ventana.add(new ejercicio6_2()); ventana.pack(); ventana.setLocationRelativeTo(null); ventana.setVisible(true); }); }
}
