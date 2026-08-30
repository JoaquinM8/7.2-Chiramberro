package unidad7;

import javax.swing.*;
import java.awt.*;

public class e7_6s extends JPanel {
    private Texto texto;
    private JTextField campoTexto;
    private JTextArea resultado;

    public static void mostrarEnVentana() {
        JFrame ventana = new JFrame("Texto");
        ventana.setSize(520, 300);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(new e7_6s());
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(e7_6s::mostrarEnVentana);
    }

    public e7_6s() {
        setLayout(new FlowLayout());
        JTextField campoLongitud = new JTextField(5);
        campoTexto = new JTextField(15);
        JButton crear = new JButton("Crear texto");
        JButton principio = new JButton("Agregar al principio");
        JButton finalTexto = new JButton("Agregar al final");
        resultado = new JTextArea(10, 45);
        resultado.setEditable(false);

        crear.addActionListener(e -> {
            try {
                texto = new Texto(Integer.parseInt(campoLongitud.getText()));
                mostrar();
            } catch (NumberFormatException error) {
                resultado.setText("Ingrese una longitud valida.");
            }
        });
        principio.addActionListener(e -> {
            if (texto != null) {
                texto.añadirPrincipio(campoTexto.getText());
                mostrar();
            }
        });
        finalTexto.addActionListener(e -> {
            if (texto != null) {
                texto.añadirFinal(campoTexto.getText());
                mostrar();
            }
        });

        add(new JLabel("Longitud maxima:")); add(campoLongitud); add(crear);
        add(new JLabel("Texto:")); add(campoTexto); add(principio); add(finalTexto);
        add(new JScrollPane(resultado));
    }

    private void mostrar() {
        resultado.setText("Texto: " + texto.mostrarTexto() + "\nVocales: " + texto.contarVocales());
    }
}
