package vista;

import dao.AlumnoDAO;
import dao.CursoDAO;
import modelo.Alumno;
import modelo.Curso;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

/**
 * Ventana principal de la aplicacion (capa vista + control).
 *
 * En un proyecto de este tamano la vista se ocupa tambien de disparar las
 * acciones del usuario y de pedir los datos a los DAO. Si el proyecto creciera,
 * el codigo de las acciones podria mudarse a un controlador aparte sin cambiar
 * los DAO ni el modelo.
 *
 * Responsabilidades:
 *   - mostrar los datos (JTable) y el formulario de edicion;
 *   - pedirle al DAO la informacion y mostrarla;
 *   - validar lo que escribe el usuario ANTES de tocar la base;
 *   - transformar SQLException en mensajes entendibles con JOptionPane.
 */
public class FrmAlumnos extends JFrame {

    /* =============================================================
       1) DEPENDENCIAS
       Se crean aca por simplicidad. En una app mas grande se
       recibirian por constructor (inyeccion de dependencias).
       ============================================================= */
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final CursoDAO  cursoDAO  = new CursoDAO();

    /* =============================================================
       2) COMPONENTES DEL FORMULARIO
       ============================================================= */
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtEdad;
    private JComboBox<Curso> cmbCurso;

    /* =============================================================
       3) COMPONENTES DE LA LISTA Y LAS HERRAMIENTAS
       ============================================================= */
    private JTable         tblAlumnos;
    private DefaultTableModel modeloTabla;
    private JTextField     txtBuscar;
    private JComboBox<Curso> cmbFiltroCurso;

    /* =============================================================
       4) BOTONES
       Nombres exactos exigidos por la Entrega 4.
       ============================================================= */
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;

    /* =============================================================
       5) RESUMEN (usa cantidadAlumnos() y promedioEdad() del DAO)
       ============================================================= */
    private JLabel lblEstadisticas;

    /** Id del alumno en edicion. 0 significa "no hay ninguna". */
    private int idEnEdicion = 0;

    private static final String[] COLUMNAS = {
            "ID", "Nombre", "Apellido", "Email", "Edad", "Curso"
    };

    private static final int ANCHO_MINIMO = 980;

    /* =============================================================
       CONSTRUCTOR
       ============================================================= */

    public FrmAlumnos() {
        setTitle("Gestión de Alumnos - Proyecto 7°");
        setSize(1180, 640);
        setMinimumSize(new Dimension(ANCHO_MINIMO, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        construirEncabezado();
        construirContenido();
        construirBarraDeBotones();

        limpiarFormulario();
        cargarCursos();
        cargarAlumnos();
    }

    /* =============================================================
       CONSTRUCCION DE LA PANTALLA
       ============================================================= */

    private void construirEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1F4E79));
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titulo = new JLabel("GESTIÓN DE ALUMNOS");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel sub = new JLabel("Base de datos: escuela_java (MySQL / MariaDB - JDBC)");
        sub.setForeground(new Color(0xC8D6E5));

        panel.add(titulo, BorderLayout.WEST);
        panel.add(sub, BorderLayout.EAST);

        add(panel, BorderLayout.NORTH);
    }

    private void construirContenido() {
        // LEFT: formulario de alta / edicion
        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBorder(BorderFactory.createTitledBorder("Datos del alumno"));
        izquierda.add(construirFormulario(), BorderLayout.CENTER);

        // RIGHT: herramientas de busqueda + tabla + resumen
        JPanel derecha = new JPanel(new BorderLayout(0, 10));
        derecha.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        derecha.add(construirBuscador(), BorderLayout.NORTH);
        derecha.add(construirTabla(), BorderLayout.CENTER);
        derecha.add(construirResumen(), BorderLayout.SOUTH);

        // Se reparten el espacio: 40% formulario, 60% tabla
        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        divisor.setResizeWeight(0.40);
        divisor.setBorder(null);
        divisor.setDividerLocation(400);

        add(divisor, BorderLayout.CENTER);
    }

    /** Formulario de carga, armado con GridBagLayout. */
    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        txtId       = new JTextField(10);
        txtId.setEditable(false);
        txtId.setBackground(new Color(0xEDEDED));

        txtNombre   = new JTextField(18);
        txtApellido = new JTextField(18);
        txtEmail    = new JTextField(18);
        txtEdad     = new JTextField(5);
        cmbCurso    = new JComboBox<>();

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;

        agregarCampo(panel, g, 0, "ID:",       txtId);
        agregarCampo(panel, g, 1, "Nombre:",   txtNombre);
        agregarCampo(panel, g, 2, "Apellido:", txtApellido);
        agregarCampo(panel, g, 3, "Email:",    txtEmail);
        agregarCampo(panel, g, 4, "Edad:",     txtEdad);
        agregarCampo(panel, g, 5, "Curso:",    cmbCurso);

        // Empuja el contenido hacia arriba del panel.
        g.gridx = 0;
        g.gridy = 6;
        g.weighty = 1;
        g.fill   = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalGlue(), g);

        return panel;
    }

    /** Coloca una etiqueta y su campo en una fila del formulario. */
    private void agregarCampo(JPanel panel, GridBagConstraints g, int fila, String etiqueta, JComponent campo) {
        g.gridx     = 0;
        g.gridy     = fila;
        g.gridwidth = 1;
        g.weightx   = 0;
        g.anchor    = GridBagConstraints.LINE_END;
        g.fill      = GridBagConstraints.NONE;

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, g);

        g.gridx   = 1;
        g.weightx = 1;
        g.fill    = GridBagConstraints.HORIZONTAL;
        panel.add(campo, g);
    }

    /** Barra de busqueda: texto libre y filtro por curso. */
    private JPanel construirBuscador() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Buscar"));

        txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Busca por nombre, apellido o email");
        txtBuscar.addActionListener(e -> aplicarFiltro());

        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> aplicarFiltro());

        JPanel filaTexto = new JPanel(new BorderLayout(6, 0));
        filaTexto.add(new JLabel("Texto:"), BorderLayout.WEST);
        filaTexto.add(txtBuscar, BorderLayout.CENTER);

        cmbFiltroCurso = new JComboBox<>();
        cmbFiltroCurso.addActionListener(e -> {
            // No filtrar mientras se arma la lista de cursos.
            if (cmbFiltroCurso.getSelectedIndex() > 0) {
                aplicarFiltro();
            }
        });

        btnMostrarTodos = new JButton("Mostrar todos");
        btnMostrarTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cmbFiltroCurso.setSelectedIndex(0);
            cargarAlumnos();
        });

        JPanel filaCurso = new JPanel(new BorderLayout(6, 0));
        filaCurso.add(new JLabel("Curso:"), BorderLayout.WEST);
        filaCurso.add(cmbFiltroCurso, BorderLayout.CENTER);

        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botonera.add(btnBuscar);
        botonera.add(btnMostrarTodos);

        panel.add(filaTexto, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 0));
        sur.add(filaCurso, BorderLayout.CENTER);
        sur.add(botonera, BorderLayout.EAST);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    /** Tabla de resultados. No se edita con doble clic. */
    private JPanel construirTabla() {
        // Se sobreescribe isCellEditable para que la tabla sea de solo lectura:
        // la edicion se hace unicamente desde el formulario de la izquierda.
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                // Sin esto el sorter trata todo como texto y ordena
                // los números alfabéticamente ("10" antes que "2").
                switch (columna) {
                    case 0:   // ID
                    case 4:   // Edad
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };

        tblAlumnos = new JTable(modeloTabla);
        tblAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAlumnos.setRowHeight(24);
        tblAlumnos.setFont(new Font("Arial", Font.PLAIN, 12));
        tblAlumnos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblAlumnos.setAutoCreateRowSorter(true);

        tblAlumnos.getSelectionModel().addListSelectionListener(evento -> {
            // Solo reaccionar cuando la seleccion ya quedo establecida.
            if (!evento.getValueIsAdjusting()) {
                cargarFormularioDesdeTabla();
            }
        });

        JScrollPane scroll = new JScrollPane(tblAlumnos);
        scroll.setPreferredSize(new Dimension(680, 420));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /** Panel inferior con el total de alumnos y el promedio de edad. */
    private JPanel construirResumen() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Resumen"));

        lblEstadisticas = new JLabel("Alumnos: -   |   Promedio de edad: -");
        lblEstadisticas.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblEstadisticas);
        return panel;
    }

    /**
     * Pide al DAO las estadisticas de la tabla alumnos (COUNT y AVG)
     * y las muestra en la barra de resumen.
     */
    private void actualizarEstadisticas() {
        try {
            int total = alumnoDAO.cantidadAlumnos();
            double promedio = alumnoDAO.promedioEdad();

            String promTexto = (total == 0)
                    ? "-"
                    : String.valueOf(Math.round(promedio * 10.0) / 10.0);

            lblEstadisticas.setText("Alumnos: " + total
                    + "   |   Promedio de edad: " + promTexto + " años");

        } catch (SQLException e) {
            lblEstadisticas.setText("Alumnos: -   |   Promedio de edad: -");
            mostrarError(e);
        }
    }

    /** Barra inferior con las acciones. */
    private void construirBarraDeBotones() {
        btnNuevo      = new JButton("Nuevo");
        btnGuardar    = new JButton("Guardar");
        btnModificar  = new JButton("Modificar");
        btnEliminar   = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> {
            limpiarFormulario();
            txtNombre.requestFocusInWindow();
        });
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        for (JButton boton : new JButton[]{btnNuevo, btnGuardar, btnModificar, btnEliminar}) {
            boton.setFont(new Font("Arial", Font.BOLD, 12));
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.setPreferredSize(new Dimension(110, 32));
            panel.add(boton);
        }

        add(panel, BorderLayout.SOUTH);
    }

    /* =============================================================
       5) CARGA DE DATOS
       ============================================================= */

    /** Llena el JComboBox del formulario y del filtro con los cursos. */
    private void cargarCursos() {
        try {
            List<Curso> cursos = cursoDAO.listar();

            // Cada combo usa SU PROPIO modelo. Compartir el mismo modelo
            // haria que "(Todos los cursos)" tambien aparezca en el
            // formulario, que solo debe ofrecer cursos reales.
            DefaultComboBoxModel<Curso> modeloFormulario = new DefaultComboBoxModel<>();
            DefaultComboBoxModel<Curso> modeloFiltro      = new DefaultComboBoxModel<>();

            // El filtro arranca con la opcion "no filtrar".
            modeloFiltro.addElement(new Curso(0, "(Todos los cursos)"));

            for (Curso curso : cursos) {
                modeloFormulario.addElement(curso);
                modeloFiltro.addElement(curso);
            }

            cmbCurso.setModel(modeloFormulario);
            cmbCurso.setSelectedIndex(0);

            cmbFiltroCurso.setModel(modeloFiltro);
            cmbFiltroCurso.setSelectedIndex(0);

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /** Recarga la tabla con todos los alumnos. */
    private void cargarAlumnos() {
        try {
            llenarTabla(alumnoDAO.listar());
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Aplica el filtro pedido: primero busca por texto; si no hay texto,
     * filtra por curso; y si no hay filtro, muestra todo.
     */
    private void aplicarFiltro() {
        String texto = txtBuscar.getText().trim();

        try {
            if (!texto.isEmpty()) {
                llenarTabla(alumnoDAO.buscar(texto));
                return;
            }
            Curso filtro = (Curso) cmbFiltroCurso.getSelectedItem();
            if (filtro != null && filtro.getId() > 0) {
                llenarTabla(alumnoDAO.buscarPorCurso(filtro.getNombre()));
                return;
            }
            llenarTabla(alumnoDAO.listar());

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /** Vuelca la lista de alumnos en el modelo de la tabla. */
    private void llenarTabla(List<Alumno> alumnos) {
        modeloTabla.setRowCount(0);   // vacia la tabla

        for (Alumno alumno : alumnos) {
            modeloTabla.addRow(new Object[]{
                    alumno.getId(),
                    alumno.getNombre(),
                    alumno.getApellido(),
                    alumno.getEmail(),
                    alumno.getEdad(),
                    alumno.getCurso()          // atributo String del POJO
            });
        }

        // El resumen siempre refleja la base completa (COUNT y AVG).
        actualizarEstadisticas();
    }

    /* =============================================================
       6) ACCIONES
       ============================================================= */

    /** Inserta un alumno nuevo. */
    private void guardar() {
        String error = validar();
        if (error != null) {
            avisarValidacion(error);
            return;
        }

        Alumno alumno = leerFormulario();
        try {
            // Aviso previo de email duplicado, para un mensaje mas claro.
            if (alumnoDAO.emailEnUso(alumno.getEmail(), 0)) {
                avisarValidacion("Ya existe un alumno con el email " + alumno.getEmail() + ".");
                return;
            }

            boolean insertado = alumnoDAO.insertar(alumno);   // devuelve boolean
            if (!insertado) {
                avisarValidacion("No se pudo registrar el alumno. Revisá los datos e intentá otra vez.");
                return;
            }

            informar("Alumno " + alumno.getNombreCompleto() + " registrado correctamente.");

            limpiarFormulario();
            aplicarFiltro();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /** Actualiza el alumno que estaba seleccionado. */
    private void actualizar() {
        if (idEnEdicion == 0) {
            avisarValidacion("Primero seleccioná un alumno de la tabla.");
            return;
        }

        String error = validar();
        if (error != null) {
            avisarValidacion(error);
            return;
        }

        Alumno alumno = leerFormulario();
        try {
            if (alumnoDAO.emailEnUso(alumno.getEmail(), idEnEdicion)) {
                avisarValidacion("Ese email ya pertenece a otro alumno.");
                return;
            }

            alumno.setId(idEnEdicion);
            boolean modificado = alumnoDAO.actualizar(alumno);   // devuelve boolean
            if (!modificado) {
                avisarValidacion("No se pudo modificar: el registro ya no existe en la base.");
                return;
            }

            informar("Datos de " + alumno.getNombreCompleto() + " actualizados.");

            limpiarFormulario();
            aplicarFiltro();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /** Elimina el alumno seleccionado, previa confirmacion. */
    private void eliminar() {
        if (idEnEdicion == 0) {
            avisarValidacion("Primero seleccioná un alumno de la tabla.");
            return;
        }

        String nombre = txtNombre.getText().trim() + " " + txtApellido.getText().trim();
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que querés eliminar a " + nombre + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = alumnoDAO.eliminar(idEnEdicion);   // devuelve boolean
            if (!eliminado) {
                avisarValidacion("No se pudo eliminar: el registro ya no existe en la base.");
                return;
            }

            informar("Alumno eliminado.");

            limpiarFormulario();
            aplicarFiltro();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /* =============================================================
       7) FORMULARIO
       ============================================================= */

    /** Vuelca en el formulario los datos de la fila seleccionada. */
    private void cargarFormularioDesdeTabla() {
        int fila = tblAlumnos.getSelectedRow();
        if (fila < 0) {
            return;
        }

        // Con el table sorter activo, hay que traducir la fila visible
        // a la fila real del modelo antes de leer los datos.
        int indice = tblAlumnos.convertRowIndexToModel(fila);

        idEnEdicion = (Integer) modeloTabla.getValueAt(indice, 0);
        txtId.setText(String.valueOf(idEnEdicion));
        txtNombre.setText(valorTexto(indice, 1));
        txtApellido.setText(valorTexto(indice, 2));
        txtEmail.setText(valorTexto(indice, 3));
        txtEdad.setText(valorTexto(indice, 4));

        // Selecciona en el combo el curso que coincide por nombre.
        String nombreCurso = valorTexto(indice, 5);
        for (int i = 0; i < cmbCurso.getItemCount(); i++) {
            Curso curso = cmbCurso.getItemAt(i);
            if (curso.getNombre().equals(nombreCurso)) {
                cmbCurso.setSelectedIndex(i);
                break;
            }
        }
    }

    /** Vacia el formulario y deja el modo "alta". */
    private void limpiarFormulario() {
        idEnEdicion = 0;
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtEdad.setText("");
        if (cmbCurso.getItemCount() > 0) {
            cmbCurso.setSelectedIndex(0);
        }
        tblAlumnos.clearSelection();
    }

    /** Arma un objeto Alumno con lo que hay escrito en el formulario. */
    private Alumno leerFormulario() {
        Curso curso = (Curso) cmbCurso.getSelectedItem();
        if (curso == null) {
            curso = new Curso();
        }

        Alumno alumno = new Alumno(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtEmail.getText().trim(),
                Integer.parseInt(txtEdad.getText().trim()),
                curso.getNombre());          // atributo curso (String) del POJO

        alumno.setCursoId(curso.getId());    // clave foranea curso_id
        return alumno;
    }

    /**
     * Revisa los datos del formulario ANTES de mandarlos a la base.
     *
     * @return el mensaje con el error, o null si todo esta bien.
     */
    private String validar() {
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String email     = txtEmail.getText().trim();
        String edadTexto = txtEdad.getText().trim();

        if (nombre.isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (apellido.isEmpty()) {
            return "El apellido es obligatorio.";
        }
        if (email.isEmpty()) {
            return "El email es obligatorio.";
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")) {
            return "El email no tiene un formato válido. Ejemplo: ana.lopez@escuela.edu";
        }
        if (edadTexto.isEmpty()) {
            return "La edad es obligatoria.";
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException e) {
            return "La edad debe ser un número entero.";
        }
        if (edad <= 0 || edad > 120) {
            return "La edad debe ser un número mayor a 0 y hasta 120.";
        }

        Curso curso = (Curso) cmbCurso.getSelectedItem();
        if (curso == null || curso.getId() == 0) {
            return "Seleccioná un curso.";
        }

        return null;
    }

    /** Lee una celda del modelo como texto, tolerando valores nulos. */
    private String valorTexto(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    /* =============================================================
       8) MENSAJES AL USUARIO
       ============================================================= */

    private void informar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void avisarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Revisar los datos",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Convierte una SQLException en un mensaje entendible.
     * Nunca se muestra el detalle tecno al usuario final.
     */
    private void mostrarError(SQLException e) {
        System.err.println("Error de base de datos: " + e.getMessage());

        String mensaje;
        int codigo = e.getErrorCode();
        if (codigo == 1062) {
            mensaje = "Ya existe un registro con ese email.";
        } else if (codigo == 1451 || codigo == 1452) {
            mensaje = "No se puede completar la operación: hay datos relacionados.";
        } else {
            mensaje = "No se pudo completar la operación.\n"
                    + "Revisá que XAMPP esté iniciado y que la base 'escuela_java' exista.";
        }

        JOptionPane.showMessageDialog(this, mensaje, "Error de base de datos",
                JOptionPane.ERROR_MESSAGE);
    }
}