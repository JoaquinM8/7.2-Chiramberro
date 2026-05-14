import javax.swing.*;
import java.awt.*;

public class TP1 extends JFrame {

    private CardLayout navegador = new CardLayout(); // Navegador para cambiar entre paneles
    private JPanel contenedor = new JPanel(navegador); // Contenedor principal que usará el CardLayout

    public TP1() {
        setTitle("TP 1 - Menú Principal");
        setSize(720, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contenedor.add(crearMenu(), "MenuPrincipal");
        contenedor.add(Redondeo(), "Redondeo");

        add(contenedor);
        setVisible(true);
    }

    // Método para cambiar de pantalla
    private void mostrarPantalla(String nombre) {
        navegador.show(contenedor, nombre);
    
        switch (nombre) {
            case "MenuPrincipal":
                setTitle("TP 1 - Menú Principal");
                break;
            case "Redondeo":
                setTitle("TP 1 - Redondeo");
                break;
            case "VentasFijas":
                setTitle("TP 1 - Ventas Fijas");
                break;
            case "VentasVariables":
                setTitle("TP 1 - Ventas Variables");
                break;
            case "Circunferencia":
                setTitle("TP 1 - Circunferencia");
                break;
            case "Temperatura":
                setTitle("TP 1 - Temperatura");
                break;
            case "Velocidad":
                setTitle("TP 1 - Velocidad");
                break;
            case "Hipotenusa":
                setTitle("TP 1 - Hipotenusa");
                break;
            case "AreaTriangulo":
                setTitle("TP 1 - Área de Triángulo");
                break;
            case "Descomposicion":
                setTitle("TP 1 - Descomposición de Número");
                break;
            case "NumeroSuerte":
                setTitle("TP 1 - Número de la Suerte");
                break;
    }
    }

    private JPanel crearMenu() {

        JPanel panel = new JPanel(new BorderLayout());

        JButton btnEx1 = new JButton("Redondeo de decimales");
        JButton btnEx2 = new JButton("Cálculo de ventas (Valores fijos)");
        JButton btnEx3 = new JButton("Cálculo de ventas (Valores variables)");
        JButton btnEx4 = new JButton("Cálculo de circunferencia");
        JButton btnEx5 = new JButton("Conversión de temperatura");
        JButton btnEx6 = new JButton("Conversión de velocidad");
        JButton btnEx7 = new JButton("Cálculo de hipotenusa");
        JButton btnEx8 = new JButton("Área de triángulo");
        JButton btnEx9 = new JButton("Descomposición de número");
        JButton btnEx10 = new JButton("Número de la suerte");

        btnEx1.addActionListener(e -> mostrarPantalla("Redondeo"));
        btnEx2.addActionListener(e -> mostrarPantalla("VentasFijas"));
        btnEx3.addActionListener(e -> mostrarPantalla("VentasVariables"));
        btnEx4.addActionListener(e -> mostrarPantalla("Circunferencia"));
        btnEx5.addActionListener(e -> mostrarPantalla("Temperatura"));
        btnEx6.addActionListener(e -> mostrarPantalla("Velocidad"));
        btnEx7.addActionListener(e -> mostrarPantalla("Hipotenusa"));
        btnEx8.addActionListener(e -> mostrarPantalla("AreaTriangulo"));
        btnEx9.addActionListener(e -> mostrarPantalla("Descomposicion"));
        btnEx10.addActionListener(e -> mostrarPantalla("NumeroSuerte"));

        JPanel paneltitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paneltitulo.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );
        paneltitulo.add(new JLabel("Seleccione un ejercicio:"));
        
        JPanel panelbotones = new JPanel(new GridLayout(5, 2, 10, 10));
        panelbotones.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );
        panelbotones.add(btnEx1);
        panelbotones.add(btnEx2);
        panelbotones.add(btnEx3);
        panelbotones.add(btnEx4);
        panelbotones.add(btnEx5);
        panelbotones.add(btnEx6);
        panelbotones.add(btnEx7);
        panelbotones.add(btnEx8);
        panelbotones.add(btnEx9);
        panelbotones.add(btnEx10);

        panel.add(paneltitulo, BorderLayout.NORTH);
        panel.add(panelbotones, BorderLayout.CENTER);

        return panel;
    }

    private JPanel Redondeo() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField txt1 = new JTextField(5);
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel lblTitulo = new JLabel("Número:" , SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        txt1.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 15));
        lblResultado.setForeground(Color.GREEN.darker());
                // Center panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(120, 10, 120, 10)
            )
            )
        );
        centerPanel.setBackground(new Color(0, 240, 240));

        centerPanel.add(lblTitulo);
        centerPanel.add(txt1);
        centerPanel.add(lblResultado);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                double n1 = Double.parseDouble(txt1.getText());
                Integer suma = (int) Math.round(n1);
                lblResultado.setText("Redondeo: " + suma);
            } catch (NumberFormatException ex) {
                lblResultado.setText("Error: Ingrese un número válido");
                lblResultado.setFont(new Font("Arial", Font.ITALIC, 15));
                lblResultado.setForeground(Color.RED);
            }
        });

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }






















    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TP1());
    }

}