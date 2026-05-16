import javax.swing.*;
import java.awt.*;

public class TP1 extends JFrame { // Clase principal que extiende JFrame para crear la ventana de la aplicación

    private CardLayout navegador = new CardLayout(); // Navegador para cambiar entre paneles
    private JPanel contenedor = new JPanel(navegador); // Contenedor principal que usará el CardLayout

    public TP1() {
        setTitle("TP 1 - Menú Principal"); // Titulo inicial
        setSize(720, 480); // Tamaño de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Cerrar la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla

        // Agregar las distintas pantallas al contenedor (Método y nombre del panel para el CardLayout)
        contenedor.add(crearMenu(), "MenuPrincipal"); 
        contenedor.add(Redondeo(), "Redondeo");
        contenedor.add(VentasFijas(), "VentasFijas");
        contenedor.add(VentasVariables(), "VentasVariables");
        contenedor.add(Circunferencia(), "Circunferencia");
        contenedor.add(Temperatura(), "Temperatura");
        contenedor.add(Velocidad(), "Velocidad");
        contenedor.add(Hipotenusa(), "Hipotenusa");
        contenedor.add(AreaTriangulo(), "AreaTriangulo");
        contenedor.add(Descomposicion(), "Descomposicion");
        contenedor.add(NumeroSuerte(), "NumeroSuerte");

        // Agregar el contenedor al JFrame y mostrar la ventana
        add(contenedor);
        setVisible(true);
    }

    // Método para cambiar de pantalla
    private void mostrarPantalla(String nombre) {
        navegador.show(contenedor, nombre);
    
    // Cambiar el título de la ventana según la pantalla mostrada
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

    // Método para crear el menú principal con botones para cada ejercicio
    private JPanel crearMenu() {

        JPanel panel = new JPanel(new BorderLayout());

        JButton btnEx1 = new JButton("Redondeo de decimales");
        btnEx1.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx2 = new JButton("Cálculo de ventas (Valores fijos)");
        btnEx2.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx3 = new JButton("Cálculo de ventas (Valores variables)");
        btnEx3.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx4 = new JButton("Cálculo de circunferencia");
        btnEx4.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx5 = new JButton("Conversión de temperatura");
        btnEx5.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx6 = new JButton("Conversión de velocidad");
        btnEx6.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx7 = new JButton("Cálculo de hipotenusa");
        btnEx7.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx8 = new JButton("Área de triángulo");
        btnEx8.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx9 = new JButton("Descomposición de número");
        btnEx9.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnEx10 = new JButton("Número de la suerte");
        btnEx10.setFont(new Font("Arial", Font.BOLD, 14));

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
        JLabel lblTitulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        paneltitulo.add(lblTitulo);

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


    // Métodos para crear cada pantalla de ejercicio
    private JPanel Redondeo() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField numField = new JTextField(5);
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel lblTitulo = new JLabel("Número:" , SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        numField.setFont(new Font("Arial", Font.PLAIN, 12));

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
        centerPanel.add(numField);
        centerPanel.add(lblResultado);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                double n1 = Double.parseDouble(numField.getText());
                Integer suma = (int) Math.round(n1);
                lblResultado.setText("Redondeo: " + suma);
            } catch (NumberFormatException ex) {
                lblResultado.setText("Ingrese un número válido");
                lblResultado.setFont(new Font("Arial", Font.ITALIC, 12));
                lblResultado.setForeground(Color.RED);
            }
        });

        btnVolver.addActionListener(e -> {
            numField.setText("");
            lblResultado.setText("");

            mostrarPantalla("MenuPrincipal");
        });
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
        panelbotonesManzanas.add(new JLabel("1º Trimestre: ", SwingConstants.CENTER));
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
        panelbotonesPeras.add(new JLabel("1º Trimestre: ", SwingConstants.CENTER));
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

        btnVolver.addActionListener(e -> {
            inputField.setText("");
            inputField2.setText("");
            inputField3.setText("");
            inputField4.setText("");
            inputField5.setText("");
            inputField6.setText("");
            resultLabel.setText("Los beneficios anuales son: ");

            mostrarPantalla("MenuPrincipal");
        });
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

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(btnCalcular);
        controlPanel.add(btnVolver);

        bottomPanel.add(resultLabel);
        bottomPanel.add(controlPanel);
        
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

        btnVolver.addActionListener(e -> {
            inputField.setText("");
            inputField2.setText("");
            inputField3.setText("");
            inputField4.setText("");
            inputField5.setText("");
            inputField6.setText("");
            precioMan1.setText("");
            precioMan2.setText("");
            precioMan3.setText("");
            precioPer1.setText("");
            precioPer2.setText("");
            precioPer3.setText("");
            resultLabel.setText("Los beneficios anuales son: ");

            mostrarPantalla("MenuPrincipal");
        });
        panel.add(panelVentas, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel Circunferencia() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel lblTitulo = new JLabel("Radio:", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 40));

        JTextField rField = new JTextField(10);
        rField.setHorizontalAlignment(SwingConstants.CENTER);
        rField.setFont(new Font("Arial", Font.PLAIN, 20));
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
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
        centerPanel.add(rField);


        btnCalcular.addActionListener(e -> {
            try {
                double r = Double.parseDouble(rField.getText());
                double circLong = 2 * Math.PI * r;
                double circArea = Math.PI * r * r;
                JOptionPane.showMessageDialog(panel, "La circunferencia es: " + String.format("%.2f", circLong) + "\n" + "El área es: " + String.format("%.2f", circArea), "Resultados", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido para el radio.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnVolver.addActionListener(e -> {
            rField.setText("");

            mostrarPantalla("MenuPrincipal");
        });
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel Temperatura() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");


        JLabel lblTitulo = new JLabel("Temperatura en °C:", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField tempField = new JTextField(10);
        tempField.setHorizontalAlignment(SwingConstants.CENTER);
        tempField.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblResultado = new JLabel("Temperatura en ºF:", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );
        centerPanel.add(lblTitulo);
        centerPanel.add(tempField);
        centerPanel.add(lblResultado);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                double tempC = Double.parseDouble(tempField.getText());
                double tempF = (tempC * 9/5) + 32;
                lblResultado.setText(String.format("Temperatura en ºF: %.2f", tempF));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            tempField.setText("");
            lblResultado.setText("Temperatura en ºF:");

            mostrarPantalla("MenuPrincipal");
        });        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel Velocidad() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");


        JLabel lblTitulo = new JLabel("Velocidad (km/h):", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField velField = new JTextField(10);
        velField.setHorizontalAlignment(SwingConstants.CENTER);
        velField.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblResultado = new JLabel("Velocidad (m/s):", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );
        centerPanel.add(lblTitulo);
        centerPanel.add(velField);
        centerPanel.add(lblResultado);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                double velKmH = Double.parseDouble(velField.getText());
                double velMs = velKmH / 3.6;
                lblResultado.setText(String.format("Velocidad (m/s): %.2f", velMs));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            velField.setText("");
            lblResultado.setText("Velocidad (m/s):");

            mostrarPantalla("MenuPrincipal");
        });        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel Hipotenusa() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel lblTitulo = new JLabel("Calculadora de Hipotenusa", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel lblCat1 = new JLabel("Cateto 1:", SwingConstants.CENTER);
        lblCat1.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblCat2 = new JLabel("Cateto 2:", SwingConstants.CENTER);
        lblCat2.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField cat1Field = new JTextField(10);
        cat1Field.setHorizontalAlignment(SwingConstants.CENTER);
        cat1Field.setFont(new Font("Arial", Font.PLAIN, 15));
        JTextField cat2Field = new JTextField(10);
        cat2Field.setHorizontalAlignment(SwingConstants.CENTER);
        cat2Field.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblResultado = new JLabel("Hipotenusa:", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.add(lblCat1);
        inputPanel.add(cat1Field);
        inputPanel.add(lblCat2);
        inputPanel.add(cat2Field);

        centerPanel.add(lblTitulo);
        centerPanel.add(inputPanel);
        centerPanel.add(lblResultado);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                double cat1 = Double.parseDouble(cat1Field.getText());
                double cat2 = Double.parseDouble(cat2Field.getText());
                double hipotenusa = Math.hypot(cat1, cat2);
                lblResultado.setText(String.format("Hipotenusa: %.2f", hipotenusa));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            cat1Field.setText("");
            cat2Field.setText("");
            lblResultado.setText("Hipotenusa:");

            mostrarPantalla("MenuPrincipal");
        });        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel AreaTriangulo() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel lblTitulo = new JLabel("Calculadora de Área de Triángulo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel lblLado1 = new JLabel("Lado 1:", SwingConstants.CENTER);
        lblLado1.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblLado2 = new JLabel("Lado 2:", SwingConstants.CENTER);
        lblLado2.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblLado3 = new JLabel("Lado 3:", SwingConstants.CENTER);
        lblLado3.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField lado1Field = new JTextField(10);
        lado1Field.setHorizontalAlignment(SwingConstants.CENTER);
        lado1Field.setFont(new Font("Arial", Font.PLAIN, 15));
        JTextField lado2Field = new JTextField(10);
        lado2Field.setHorizontalAlignment(SwingConstants.CENTER);
        lado2Field.setFont(new Font("Arial", Font.PLAIN, 15));
        JTextField lado3Field = new JTextField(10);
        lado3Field.setHorizontalAlignment(SwingConstants.CENTER);
        lado3Field.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblResultado = new JLabel("Área:", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(lblLado1);
        inputPanel.add(lado1Field);
        inputPanel.add(lblLado2);
        inputPanel.add(lado2Field);
        inputPanel.add(lblLado3);
        inputPanel.add(lado3Field);

        centerPanel.add(lblTitulo);
        centerPanel.add(inputPanel);
        centerPanel.add(lblResultado);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                double lado1 = Double.parseDouble(lado1Field.getText());
                double lado2 = Double.parseDouble(lado2Field.getText());
                double lado3 = Double.parseDouble(lado3Field.getText());

                double s = (lado1 + lado2 + lado3) / 2;
                double area = Math.sqrt(s * (s - lado1) * (s - lado2) * (s - lado3));
                lblResultado.setText(String.format("Área: %.2f", area));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            lado1Field.setText("");
            lado2Field.setText("");
            lado3Field.setText("");
            lblResultado.setText("Área:");

            mostrarPantalla("MenuPrincipal");
        });
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel Descomposicion() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");


        JLabel lblTitulo = new JLabel("Ingresar un número de 3 cifras:", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 25));

        JTextField numField = new JTextField(10);
        numField.setHorizontalAlignment(SwingConstants.CENTER);
        numField.setFont(new Font("Arial", Font.PLAIN, 15));

        JPanel resultPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JLabel lblCentenas = new JLabel("Primera cifra", SwingConstants.CENTER);
        lblCentenas.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblC1 = new JLabel("", SwingConstants.CENTER);
        lblC1.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblDecenas = new JLabel("Cifra central", SwingConstants.CENTER);
        lblDecenas.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblC2 = new JLabel("", SwingConstants.CENTER);
        lblC2.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblUnidades = new JLabel("Última cifra", SwingConstants.CENTER);
        lblUnidades.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblC3 = new JLabel("", SwingConstants.CENTER);
        lblC3.setFont(new Font("Arial", Font.BOLD, 20));

        resultPanel.add(lblCentenas);
        resultPanel.add(lblDecenas);
        resultPanel.add(lblUnidades);

        resultPanel.add(lblC1);
        resultPanel.add(lblC2);
        resultPanel.add(lblC3);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
            )
        );
        centerPanel.add(lblTitulo);
        centerPanel.add(numField);
        centerPanel.add(resultPanel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                int num = Integer.parseInt(numField.getText());
                if (num < 100 || num > 999) {
                    JOptionPane.showMessageDialog(panel, "Ingrese un número de 3 cifras.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int unidades = num % 10;
                int decenas = (num / 10) % 10;
                int centenas = num / 100;
                lblC1.setText("" + centenas);
                lblC2.setText("" + decenas);
                lblC3.setText("" + unidades);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            lblC1.setText("");
            lblC2.setText("");
            lblC3.setText("");
            numField.setText("");

            mostrarPantalla("MenuPrincipal");
        });
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel NumeroSuerte() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCalcular = new JButton("Calcular");
        JButton btnVolver = new JButton("Volver");

        JLabel lblTitulo = new JLabel("Ingresar fecha de nacimiento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel lblAño = new JLabel("Año:", SwingConstants.CENTER);
        lblAño.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblMes = new JLabel("Mes:", SwingConstants.CENTER);
        lblMes.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblDía = new JLabel("Día:", SwingConstants.CENTER);
        lblDía.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField añoField = new JTextField(5);
        añoField.setHorizontalAlignment(SwingConstants.CENTER);
        añoField.setFont(new Font("Arial", Font.PLAIN, 15));
        JTextField mesField = new JTextField(5);
        mesField.setHorizontalAlignment(SwingConstants.CENTER);
        mesField.setFont(new Font("Arial", Font.PLAIN, 15));
        JTextField díaField = new JTextField(5);
        díaField.setHorizontalAlignment(SwingConstants.CENTER);
        díaField.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblResultado = new JLabel("Número de la Suerte:", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Arial", Font.BOLD, 20));


        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
            )
        );

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; 
        inputPanel.add(lblAño, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(añoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(lblMes, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(mesField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(lblDía, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(díaField, gbc);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.insets = new Insets(10, 10, 10, 10);
        gbcCenter.gridx = 0;

        gbcCenter.gridy = 0;
        centerPanel.add(lblTitulo, gbcCenter);

        gbcCenter.gridy = 1;
        centerPanel.add(inputPanel, gbcCenter);

        gbcCenter.gridy = 2;
        centerPanel.add(lblResultado, gbcCenter);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.add(btnCalcular);
        bottomPanel.add(btnVolver);

        btnCalcular.addActionListener(e -> {
            try {
                int año = Integer.parseInt(añoField.getText());
                int mes = Integer.parseInt(mesField.getText());
                int día = Integer.parseInt(díaField.getText());

                int s = año + mes + día;
                int luckyNum = s % 10 + s / 10 % 10 + s / 100 % 10 + s / 1000;
                lblResultado.setText("Número de la Suerte: " + luckyNum);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            añoField.setText("");
            mesField.setText("");
            díaField.setText("");
            lblResultado.setText("Número de la Suerte:"); 

            mostrarPantalla("MenuPrincipal");
        });
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TP1());
    }

}