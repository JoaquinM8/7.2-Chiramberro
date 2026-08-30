package unidad7;

import javax.swing.*;
import java.awt.*;


public class e7_1s extends JFrame {

    // -------------------------------------------------
    // COMPONENTES DE LA INTERFAZ GRÁFICA
    // -------------------------------------------------

    // Campo de texto para ingresar el nombre.
    private JTextField txtNombre;

    // Campo de texto para ingresar el DNI.
    private JTextField txtDni;

    // Campo de texto para ingresar el límite.
    private JTextField txtLimite;

    // Campo de texto para ingresar el importe.
    private JTextField txtImporte;

    // Campo para ingresar el nombre del banco.
    private JTextField txtBanco;

    // Campo para ingresar el capital del banco.
    private JTextField txtCapitalBanco;

    // Campo para ingresar la direccion central del banco.
    private JTextField txtDireccionBanco;

    // Área donde se mostrará la información.
    private JTextArea txtResultado;

    // Botón para crear la cuenta.
    private JButton btnCrear;

    // Botón para realizar un ingreso.
    private JButton btnIngreso;

    // Botón para realizar un egreso.
    private JButton btnEgreso;

    // Botón para mostrar la información.
    private JButton btnMostrar;

    // Botón para limpiar los campos.
    private JButton btnLimpiar;

    // Variable que representa la cuenta corriente.
    private CuentaCorriente cuenta;


    // -------------------------------------------------
    // CONSTRUCTOR DE LA VENTANA
    // -------------------------------------------------

    /**
     * Constructor de la ventana principal.
     */
    public e7_1s() {

        // Configuramos el título de la ventana.
        setTitle("Cuenta Corriente");

        // Establecemos el tamaño de la ventana.
        setSize(650, 600);

        // Indicamos que la ventana aparecerá centrada.
        setLocationRelativeTo(null);

        // Cuando el usuario cierre la ventana,
        // finaliza la ejecución del programa.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Llamamos al método que construye la interfaz.
        crearInterfaz();
    }


    // -------------------------------------------------
    // CREACIÓN DE LA INTERFAZ
    // -------------------------------------------------

    /**
     * Crea y organiza todos los componentes
     * visuales de la aplicación.
     */
    private void crearInterfaz() {

        // Creamos el panel principal.
        JPanel panelPrincipal = new JPanel();

        // Utilizamos BorderLayout para organizar
        // los diferentes sectores de la ventana.
        panelPrincipal.setLayout(new BorderLayout(10, 10));

        // Creamos un borde vacío para separar
        // los componentes de los bordes de la ventana.
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );


        // -------------------------------------------------
        // PANEL DE DATOS
        // -------------------------------------------------

        JPanel panelDatos = new JPanel();

        // Utilizamos GridLayout para colocar
        // etiquetas y campos de texto.
        panelDatos.setLayout(new GridLayout(7, 2, 5, 5));


        // Etiqueta para el nombre.
        panelDatos.add(new JLabel("Nombre:"));

        // Campo para introducir el nombre.
        txtNombre = new JTextField();
        panelDatos.add(txtNombre);


        // Etiqueta para el DNI.
        panelDatos.add(new JLabel("DNI:"));

        // Campo para introducir el DNI.
        txtDni = new JTextField();
        panelDatos.add(txtDni);


        // Etiqueta para el límite.
        panelDatos.add(new JLabel("Límite descubierto:"));

        // Campo para introducir el límite.
        txtLimite = new JTextField();
        panelDatos.add(txtLimite);


        // Etiqueta para el importe.
        panelDatos.add(new JLabel("Importe:"));

        // Campo donde se introduce el importe
        // para ingresos o retiros.
        txtImporte = new JTextField();
        panelDatos.add(txtImporte);

        panelDatos.add(new JLabel("Banco (opcional):"));
        txtBanco = new JTextField();
        panelDatos.add(txtBanco);

        panelDatos.add(new JLabel("Capital del banco:"));
        txtCapitalBanco = new JTextField();
        panelDatos.add(txtCapitalBanco);

        panelDatos.add(new JLabel("Direccion central:"));
        txtDireccionBanco = new JTextField();
        panelDatos.add(txtDireccionBanco);


        // Agregamos el panel de datos
        // en la parte superior de la ventana.
        panelPrincipal.add(panelDatos, BorderLayout.NORTH);


        // -------------------------------------------------
        // PANEL DE BOTONES
        // -------------------------------------------------

        JPanel panelBotones = new JPanel();

        // Creamos los botones.
        btnCrear = new JButton("Crear cuenta");
        btnIngreso = new JButton("Ingresar dinero");
        btnEgreso = new JButton("Retirar dinero");
        btnMostrar = new JButton("Mostrar información");
        JButton btnAsignarBanco = new JButton("Asignar banco");
        JButton btnQuitarBanco = new JButton("Quitar banco");
        btnLimpiar = new JButton("Limpiar");


        // Agregamos los botones al panel.
        panelBotones.add(btnCrear);
        panelBotones.add(btnIngreso);
        panelBotones.add(btnEgreso);
        panelBotones.add(btnMostrar);
        panelBotones.add(btnAsignarBanco);
        panelBotones.add(btnQuitarBanco);
        panelBotones.add(btnLimpiar);


        // Agregamos el panel de botones
        // en la zona central.
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);


        // -------------------------------------------------
        // ÁREA DE RESULTADOS
        // -------------------------------------------------

        // Creamos el área de texto.
        txtResultado = new JTextArea(10, 45);

        // Permitimos que el usuario no pueda
        // modificar el resultado directamente.
        txtResultado.setEditable(false);

        // Permitimos que las líneas se ajusten
        // automáticamente al ancho disponible.
        txtResultado.setLineWrap(true);

        // Ajustamos las palabras al cambiar de línea.
        txtResultado.setWrapStyleWord(true);

        // Creamos un scroll para el área de texto.
        JScrollPane scroll = new JScrollPane(txtResultado);

        // Agregamos el scroll en la parte inferior.
        panelPrincipal.add(scroll, BorderLayout.SOUTH);


        // -------------------------------------------------
        // EVENTOS DE LOS BOTONES
        // -------------------------------------------------

        // Asociamos el botón Crear con su método.
        btnCrear.addActionListener(e -> crearCuenta());


        // Asociamos el botón Ingresar con su método.
        btnIngreso.addActionListener(e -> realizarIngreso());


        // Asociamos el botón Retirar con su método.
        btnEgreso.addActionListener(e -> realizarEgreso());


        // Asociamos el botón Mostrar con su método.
        btnMostrar.addActionListener(e -> mostrarInformacion());

        btnAsignarBanco.addActionListener(e -> asignarBanco());

        btnQuitarBanco.addActionListener(e -> quitarBanco());


        // Asociamos el botón Limpiar con su método.
        btnLimpiar.addActionListener(e -> limpiar());


        // Colocamos el panel principal dentro de la ventana.
        add(panelPrincipal);
    }


    // -------------------------------------------------
    // CREAR CUENTA
    // -------------------------------------------------

    /**
     * Crea una nueva cuenta corriente
     * utilizando los datos ingresados.
     */
    private void crearCuenta() {

        // Obtenemos el nombre ingresado.
        String nombre = txtNombre.getText().trim();

        // Obtenemos el DNI ingresado.
        String dni = txtDni.getText().trim();

        // Obtenemos el importe ingresado.
        Double importe = 0.0;

        // Obtenemos el limite ingresado.
        Double limite = 0.0;

        // Gestión de creación de cuenta según los datos ingresados.
        if (dni.isEmpty() && nombre.isEmpty() && txtImporte.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Datos inválidos.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        } else if (nombre.isEmpty() && dni.isEmpty()) {     
            try {
                importe = Double.parseDouble(txtImporte.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El importe debe ser un numero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            cuenta = new CuentaCorriente(importe);
            txtLimite.setText("0");
        } else if (nombre.isEmpty()) {
            try {
                importe = Double.parseDouble(txtImporte.getText().trim());
                limite = Double.parseDouble(txtLimite.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El importe y el limite deben ser numeros.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
                cuenta = new CuentaCorriente(importe, limite, dni);
        } else if (!nombre.isEmpty() && !dni.isEmpty()) {
            cuenta = new CuentaCorriente(nombre, dni);
            txtImporte.setText("0");
            txtLimite.setText("-50");
        } else {
            JOptionPane.showMessageDialog(this, "Complete el DNI para crear una cuenta con nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Intentamos establecer el límite indicado.
        try {

            // Convertimos el texto del límite
            // a un número decimal.
            limite = Double.parseDouble(
                    txtLimite.getText()
            );

            // Asignamos el límite a la cuenta.
            cuenta.cambiarLimite(limite);

        } catch (NumberFormatException e) {

            // Si el valor no es numérico,
            // mostramos un mensaje de error.
            JOptionPane.showMessageDialog(
                    this,
                    "El límite debe ser un número.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        // Mostramos un mensaje indicando que la cuenta
        // fue creada correctamente.
        JOptionPane.showMessageDialog(
                this,
                "Cuenta creada correctamente.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE
        );


        // Mostramos los datos de la cuenta.
        mostrarInformacion();
    }


    // -------------------------------------------------
    // REALIZAR INGRESO
    // -------------------------------------------------

    /**
     * Realiza un ingreso de dinero en la cuenta.
     */
    private void asignarBanco() {
        if (cuenta == null) {
            JOptionPane.showMessageDialog(this, "Primero debe crear una cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreBanco = txtBanco.getText().trim();
        String capitalTexto = txtCapitalBanco.getText().trim();
        String direccion = txtDireccionBanco.getText().trim();

        if (nombreBanco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del banco.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Banco banco;
            if (capitalTexto.isEmpty()) {
                banco = new Banco(nombreBanco);
            } else {
                banco = new Banco(nombreBanco, Double.parseDouble(capitalTexto), direccion);
            }
            banco.cambiarDireccionCentral(direccion);
            cuenta.asignarBanco(banco);
            mostrarInformacion();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El capital debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarBanco() {
        if (cuenta == null) {
            JOptionPane.showMessageDialog(this, "Primero debe crear una cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cuenta.quitarBanco();
        mostrarInformacion();
    }

    private void realizarIngreso() {

        // Verificamos que exista una cuenta.
        if (cuenta == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe crear una cuenta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        try {

            // Convertimos el importe ingresado
            // desde String a double.
            double importe = Double.parseDouble(
                    txtImporte.getText()
            );


            // Verificamos que el importe sea positivo.
            if (importe <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "El importe debe ser mayor que cero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }


            // Realizamos el ingreso.
            cuenta.ingreso(importe);


            // Mostramos mensaje de confirmación.
            JOptionPane.showMessageDialog(
                    this,
                    "Ingreso realizado correctamente.",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );


            // Actualizamos la información mostrada.
            mostrarInformacion();


        } catch (NumberFormatException e) {

            // El importe ingresado no es válido.
            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar un importe numérico.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // -------------------------------------------------
    // REALIZAR EGRESO
    // -------------------------------------------------

    /**
     * Realiza un retiro de dinero de la cuenta.
     */
    private void realizarEgreso() {

        // Verificamos que exista una cuenta.
        if (cuenta == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe crear una cuenta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        try {

            // Convertimos el importe ingresado
            // desde texto a número.
            double importe = Double.parseDouble(
                    txtImporte.getText()
            );


            // Verificamos que el importe sea positivo.
            if (importe <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "El importe debe ser mayor que cero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }


            // Intentamos realizar el retiro.
            boolean operacionPosible = cuenta.egreso(importe);


            // Verificamos si la operación pudo realizarse.
            if (operacionPosible) {

                JOptionPane.showMessageDialog(
                        this,
                        "Retiro realizado correctamente.",
                        "Operación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No hay dinero suficiente.\n"
                        + "El límite de descubierto sería superado.",
                        "Operación rechazada",
                        JOptionPane.WARNING_MESSAGE
                );
            }


            // Actualizamos la información.
            mostrarInformacion();


        } catch (NumberFormatException e) {

            // Mostramos el error si el importe no es numérico.
            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar un importe numérico.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // -------------------------------------------------
    // MOSTRAR INFORMACIÓN
    // -------------------------------------------------

    /**
     * Muestra la información actual de la cuenta.
     */
    private void mostrarInformacion() {

        // Verificamos que exista una cuenta.
        if (cuenta == null) {

            txtResultado.setText(
                    "No existe ninguna cuenta creada."
            );

            return;
        }


        // Obtenemos la información de la cuenta
        // y la mostramos en el área de texto.
        String informacionBanco = "Sin banco vinculado.";
        if (cuenta.obtenerBanco() != null) {
            informacionBanco = cuenta.obtenerBanco().mostrarInformacion();
        }

        txtResultado.setText(
                cuenta.mostrarInformacion()
                + "\n\nBanco: " + informacionBanco
        );
    }


    // -------------------------------------------------
    // LIMPIAR
    // -------------------------------------------------

    /**
     * Limpia los campos de la interfaz.
     */
    private void limpiar() {

        // Limpiamos el nombre.
        txtNombre.setText("");

        // Limpiamos el DNI.
        txtDni.setText("");

        // Restauramos el límite.
        txtLimite.setText("");

        // Limpiamos el importe.
        txtImporte.setText("");

        // Limpiamos los datos del banco.
        txtBanco.setText("");
        txtCapitalBanco.setText("");
        txtDireccionBanco.setText("");

        // Limpiamos el resultado.
        txtResultado.setText("");

        // Eliminamos la cuenta actual.
        cuenta = null;
    }


    // -------------------------------------------------
    // MÉTODO MAIN
    // -------------------------------------------------

    /**
     * Método principal de la aplicación.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {

        // Ejecutamos la interfaz gráfica dentro
        // del Event Dispatch Thread de Swing.
        SwingUtilities.invokeLater(() -> {

            // Creamos una instancia de nuestra ventana.
            e7_1s ventana = new e7_1s();

            // Hacemos visible la ventana.
            ventana.setVisible(true);
        });
    }
}
