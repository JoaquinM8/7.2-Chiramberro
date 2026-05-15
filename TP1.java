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
        contenedor.add((VentasFijas()), "VentasFijas");
        contenedor.add(VentasVariables(), "VentasVariables");
        contenedor.add(Circunferencia(), "Circunferencia");
        contenedor.add(Temperatura(), "Temperatura");
        contenedor.add(Velocidad(), "Velocidad");
        contenedor.add(Hipotenusa(), "Hipotenusa");
        contenedor.add(AreaTriangulo(), "AreaTriangulo");
        contenedor.add(Descomposicion(), "Descomposicion");
        contenedor.add(NumeroSuerte(), "NumeroSuerte");

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
                lblResultado.setText("Ingrese un número válido");
                lblResultado.setFont(new Font("Arial", Font.ITALIC, 12));
                lblResultado.setForeground(Color.RED);
            }
        });

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel VentasFijas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel resultLabel = new JLabel("Los beneficios anuales son: ", SwingConstants.CENTER);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JTextField inputField4 = new JTextField(10);
        JTextField inputField5 = new JTextField(10);
        JTextField inputField6 = new JTextField(10);

        

        JPanel panelManzanas = new JPanel(new BorderLayout(5,10));
        panelManzanas.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        ));


        JLabel tituloManzanas = new JLabel("Venta de manzanas", SwingConstants.CENTER);
        tituloManzanas.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelbotonesManzanas = new JPanel(new GridLayout(3,2,5,10));
        panelbotonesManzanas.add(new JLabel("1ºTrimestre: ", SwingConstants.CENTER));
        panelbotonesManzanas.add(inputField);
        panelbotonesManzanas.add(new JLabel("2º Trimestre: ", SwingConstants.CENTER));
        panelbotonesManzanas.add(inputField3);
        panelbotonesManzanas.add(new JLabel("3º Trimestre: ", SwingConstants.CENTER));
        panelbotonesManzanas.add(inputField5);

        JLabel precioManzanas = new JLabel("Precio por manzana: $2.35", SwingConstants.CENTER);
        precioManzanas.setFont(new Font("Arial", Font.ITALIC, 10));

        panelManzanas.add(tituloManzanas, BorderLayout.NORTH);
        panelManzanas.add(panelbotonesManzanas, BorderLayout.CENTER);
        panelManzanas.add(precioManzanas, BorderLayout.SOUTH);


        JPanel panelPeras = new JPanel(new BorderLayout(5,10));
        panelPeras.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        ));

        JLabel tituloPeras = new JLabel("Venta de peras", SwingConstants.CENTER);
        tituloPeras.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel panelbotonesPeras = new JPanel(new GridLayout(3,2,5,10));
        panelbotonesPeras.add(new JLabel("1ºTrimestre: ", SwingConstants.CENTER));
        panelbotonesPeras.add(inputField2);
        panelbotonesPeras.add(new JLabel("2º Trimestre: ", SwingConstants.CENTER));
        panelbotonesPeras.add(inputField4);
        panelbotonesPeras.add(new JLabel("3º Trimestre: ", SwingConstants.CENTER));
        panelbotonesPeras.add(inputField6);

        JLabel precioPeras = new JLabel("Precio por pera: $1.95", SwingConstants.CENTER);
        precioPeras.setFont(new Font("Arial", Font.ITALIC, 10));

        panelPeras.add(tituloPeras, BorderLayout.NORTH);
        panelPeras.add(panelbotonesPeras, BorderLayout.CENTER);
        panelPeras.add(precioPeras, BorderLayout.SOUTH);
        
        JPanel panelVentas = new JPanel(new GridLayout(1,2,40,0));
        panelVentas.add(panelManzanas);
        panelVentas.add(panelPeras);


        btnCalcular.addActionListener(e -> {
            try {
                float man1 = Float.parseFloat(inputField.getText());
                float per1 = Float.parseFloat(inputField2.getText());
                float man2 = Float.parseFloat(inputField3.getText());
                float per2 = Float.parseFloat(inputField4.getText());
                float man3 = Float.parseFloat(inputField5.getText());
                float per3 = Float.parseFloat(inputField6.getText());

                float num = (float) ((man1 * 2.35 + per1 * 1.95) + (man2 * 2.35 + per2 * 1.95) + (man3 * 2.35 + per3 * 1.95));

                resultLabel.setText("Los beneficios anuales son: " + num);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });


        
        JPanel bottomPanel = new JPanel(new GridLayout(2,1,10,10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(btnCalcular);
        controlPanel.add(btnVolver);

        bottomPanel.add(resultLabel);
        bottomPanel.add(controlPanel);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));

        panel.add(panelVentas, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



private JPanel VentasVariables() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel resultLabel = new JLabel("Los beneficios anuales son: ", SwingConstants.CENTER);

        JTextField inputField = new JTextField(10);
        JTextField inputField2 = new JTextField(10);
        JTextField inputField3 = new JTextField(10);
        JTextField inputField4 = new JTextField(10);
        JTextField inputField5 = new JTextField(10);
        JTextField inputField6 = new JTextField(10);

        JTextField precioMan1 = new JTextField(5);
        JTextField precioMan2 = new JTextField(5);
        JTextField precioMan3 = new JTextField(5);

        JTextField precioPer1 = new JTextField(5);
        JTextField precioPer2 = new JTextField(5);
        JTextField precioPer3 = new JTextField(5);



        JPanel panelManzanas = new JPanel(new BorderLayout(5, 10));
        panelManzanas.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        ));

        JLabel tituloManzanas = new JLabel("Venta de manzanas", SwingConstants.CENTER);
        tituloManzanas.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelbotonesManzanas = new JPanel(new GridLayout(6, 2, 5, 5));
        panelbotonesManzanas.add(new JLabel("1ºTrimestre: ", SwingConstants.CENTER));
        panelbotonesManzanas.add(inputField);
        panelbotonesManzanas.add(new JLabel("Precio del kilo", SwingConstants.CENTER));
        panelbotonesManzanas.add(precioMan1);
        
        panelbotonesManzanas.add(new JLabel("2º Trimestre: ", SwingConstants.CENTER));
        panelbotonesManzanas.add(inputField3);
        panelbotonesManzanas.add(new JLabel("Precio del kilo", SwingConstants.CENTER));
        panelbotonesManzanas.add(precioMan2);
        
        panelbotonesManzanas.add(new JLabel("3º Trimestre: ", SwingConstants.CENTER));
        panelbotonesManzanas.add(inputField5);
        panelbotonesManzanas.add(new JLabel("Precio del kilo", SwingConstants.CENTER));
        panelbotonesManzanas.add(precioMan3);

        panelManzanas.add(tituloManzanas, BorderLayout.NORTH);
        panelManzanas.add(panelbotonesManzanas, BorderLayout.CENTER);

        JPanel panelPeras = new JPanel(new BorderLayout(5, 10));
        panelPeras.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        ));

        JLabel tituloPeras = new JLabel("Venta de peras", SwingConstants.CENTER);
        tituloPeras.setFont(new Font("Arial", Font.BOLD, 16));

        // Cambiado a 6 filas para incluir los labels de precio
        JPanel panelbotonesPeras = new JPanel(new GridLayout(6, 2, 5, 5));
        panelbotonesPeras.add(new JLabel("1ºTrimestre: ", SwingConstants.CENTER));
        panelbotonesPeras.add(inputField2);
        panelbotonesPeras.add(new JLabel("Precio del kilo", SwingConstants.CENTER));
        panelbotonesPeras.add(precioPer1);

        panelbotonesPeras.add(new JLabel("2º Trimestre: ", SwingConstants.CENTER));
        panelbotonesPeras.add(inputField4);
        panelbotonesPeras.add(new JLabel("Precio del kilo", SwingConstants.CENTER));
        panelbotonesPeras.add(precioPer2);

        panelbotonesPeras.add(new JLabel("3º Trimestre: ", SwingConstants.CENTER));
        panelbotonesPeras.add(inputField6);
        panelbotonesPeras.add(new JLabel("Precio del kilo", SwingConstants.CENTER));
        panelbotonesPeras.add(precioPer3);

        panelPeras.add(tituloPeras, BorderLayout.NORTH);
        panelPeras.add(panelbotonesPeras, BorderLayout.CENTER);

        JPanel panelVentas = new JPanel(new GridLayout(1, 2, 40, 0));
        panelVentas.add(panelManzanas);
        panelVentas.add(panelPeras);

        btnCalcular.addActionListener(e -> {
            try {
                float man1 = Float.parseFloat(inputField.getText());
                float per1 = Float.parseFloat(inputField2.getText());
                float man2 = Float.parseFloat(inputField3.getText());
                float per2 = Float.parseFloat(inputField4.getText());
                float man3 = Float.parseFloat(inputField5.getText());
                float per3 = Float.parseFloat(inputField6.getText());

                float pMan1 = Float.parseFloat(precioMan1.getText());
                float pMan2 = Float.parseFloat(precioMan2.getText());
                float pMan3 = Float.parseFloat(precioMan3.getText());
                float pPer1 = Float.parseFloat(precioPer1.getText());
                float pPer2 = Float.parseFloat(precioPer2.getText());
                float pPer3 = Float.parseFloat(precioPer3.getText());

                // Cálculo total usando los precios ingresados
                float num = (man1 * pMan1 + per1 * pPer1) + (man2 * pMan2 + per2 * pPer2) + (man3 * pMan3 + per3 * pPer3);

                resultLabel.setText("Los beneficios anuales son: " + num);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrada inválida. Introduce un número válido.");
            }
        });

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(btnCalcular);
        controlPanel.add(btnVolver);

        bottomPanel.add(resultLabel);
        bottomPanel.add(controlPanel);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));

        panel.add(panelVentas, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel Circunferencia() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel Temperatura() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel Velocidad() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel Hipotenusa() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel AreaTriangulo() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel Descomposicion() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    private JPanel NumeroSuerte() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> mostrarPantalla("MenuPrincipal"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TP1());
    }

}