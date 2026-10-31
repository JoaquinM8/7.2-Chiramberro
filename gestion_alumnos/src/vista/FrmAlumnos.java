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
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

/**
 * Ventana principal de la aplicación (capa vista + control).
 *
 * En un proyecto de este tamaño la vista se ocupa también de disparar las
 * acciones del usuario y de pedir los datos a los DAO. Si el proyecto creciera,
 * el código de las acciones podría mudarse a un controlador aparte sin cambiar
 * los DAO ni el modelo.
 *
 * Responsabilidades:
 *   - mostrar los datos (JTable) y el formulario de edición;
 *   - pedirle al DAO la información y mostrarla;
 *   - validar lo que escribe el usuario ANTES de tocar la base;
 *   - transformar SQLException en mensajes entendibles con JOptionPane.
 *
 * ORGANIZACIÓN DE LA VENTANA (Entrega 4, ejercicio 5):
 *
 *   ┌─────────────────────┬───────────────────────────────────────────┐
 *   │  DATOS              │  BÚSQUEDA                                │
 *   │  (formulario)       │  texto | curso | botones de búsqueda      │
 *   │                     ├───────────────────────────────────────────┤
 *   │  OPERACIONES        │  RESULTADOS                              │
 *   │  (CRUD)             │  JTable + cantidad + promedio de edad    │
 *   └─────────────────────┴───────────────────────────────────────────┘
 */
public class FrmAlumnos extends JFrame {

    /* =============================================================
       1) DEPENDENCIAS
       Se crean acá por simplicidad. En una app más grande se
       recibirían por constructor (inyección de dependencias).
       ============================================================= */
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final CursoDAO  cursoDAO  = new CursoDAO();

    /* =============================================================
       2) COMPONENTES DEL FORMULARIO (panel "Datos")
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
    private JButton btnBuscarPorCurso;
    private JButton btnLimpiarBusqueda;
    private JButton btnPromedioEdad;

    /* =============================================================
       5) RESUMEN
       - lblCantidad : COUNT(*)   -> cuántos alumnos hay registrados.
       - lblPromedio : AVG(edad)  -> edad promedio de los alumnos.
       - lblEstado   : informa cuántas filas se están mostrando.
       ============================================================= */
    private JLabel lblCantidad;
    private JLabel lblPromedio;
    private JLabel lblEstado;

    /** Id del alumno en edición. 0 significa "no hay ninguno". */
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
        construirPie();

        limpiarFormulario();
        cargarCursos();
        cargarAlumnos();
    }

    /* =============================================================
       CONSTRUCCIÓN DE LA PANTALLA
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

    /**
     * Arma el cuerpo de la ventana con los cuatro paneles de la consigna:
     * Datos, Operaciones, Búsqueda y Resultados.
     */
    private void construirContenido() {
        // ---------- COLUMNA IZQUIERDA: Datos + Operaciones ----------
        JPanel izquierda = new JPanel(new BorderLayout(0, 10));
        izquierda.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        izquierda.add(construirPanelDatos(), BorderLayout.CENTER);
        izquierda.add(construirPanelOperaciones(), BorderLayout.SOUTH);

        // ---------- COLUMNA DERECHA: Búsqueda + Resultados ----------
        JPanel derecha = new JPanel(new BorderLayout(0, 10));
        derecha.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        derecha.add(construirPanelBusqueda(), BorderLayout.NORTH);
        derecha.add(construirPanelResultados(), BorderLayout.CENTER);

        // Se reparten el espacio: 38% formulario, 62% tabla.
        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        divisor.setResizeWeight(0.38);
        divisor.setBorder(null);
        divisor.setDividerLocation(400);

        add(divisor, BorderLayout.CENTER);
    }

    /* -------------------------------------------------------------
       PANEL "DATOS" (ejercicio 5): formulario de alta / edición
       ------------------------------------------------------------- */

    private JPanel construirPanelDatos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos"));

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

    /* -------------------------------------------------------------
       PANEL "OPERACIONES" (ejercicio 5): botones del CRUD
       ------------------------------------------------------------- */

    private JPanel construirPanelOperaciones() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Operaciones"));

        btnNuevo      = new JButton("Nuevo");
        btnGuardar    = new JButton("Guardar");
        btnModificar  = new JButton("Modificar");
        btnEliminar   = new JButton("Eliminar");
        btnPromedioEdad = new JButton("Ver promedio de edad");

        btnNuevo.addActionListener(e -> {
            limpiarFormulario();
            txtNombre.requestFocusInWindow();
        });
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        // Ejercicio 4: muestra la edad promedio calculada con AVG(edad).
        btnPromedioEdad.addActionListener(e -> mostrarPromedioEdad());

        JPanel botones = new JPanel(new GridLayout(2, 2, 8, 8));
        for (JButton boton : new JButton[]{btnNuevo, btnGuardar, btnModificar, btnEliminar}) {
            boton.setFont(new Font("Arial", Font.BOLD, 12));
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.setPreferredSize(new Dimension(140, 32));
            botones.add(boton);
        }

        btnPromedioEdad.setFont(new Font("Arial", Font.PLAIN, 12));
        btnPromedioEdad.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel filaPromedio = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaPromedio.add(btnPromedioEdad);

        panel.add(botones, BorderLayout.CENTER);
        panel.add(filaPromedio, BorderLayout.SOUTH);

        return panel;
    }

    /* -------------------------------------------------------------
       PANEL "BÚSQUEDA" (ejercicios 1 y 2)
       ------------------------------------------------------------- */

    private JPanel construirPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));

        txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Busca por nombre, apellido o email");
        txtBuscar.addActionListener(e -> buscarPorTexto());

        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarPorTexto());

        cmbFiltroCurso = new JComboBox<>();

        // Ejercicio 1: búsqueda por curso con JComboBox + botón.
        btnBuscarPorCurso = new JButton("Buscar por curso");
        btnBuscarPorCurso.addActionListener(e -> buscarPorCurso());

        // Ejercicio 2: volver a mostrar todos los alumnos.
        btnLimpiarBusqueda = new JButton("Limpiar búsqueda");
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Fila 1: búsqueda por texto
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        panel.add(new JLabel("Texto:"), g);

        g.gridx = 1; g.weightx = 1;
        panel.add(txtBuscar, g);

        g.gridx = 2; g.weightx = 0;
        panel.add(btnBuscar, g);

        // Fila 2: búsqueda por curso
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        panel.add(new JLabel("Curso:"), g);

        g.gridx = 1; g.weightx = 1;
        panel.add(cmbFiltroCurso, g);

        g.gridx = 2; g.weightx = 0;
        panel.add(btnBuscarPorCurso, g);

        // Fila 3: limpiar la búsqueda
        g.gridx = 0; g.gridy = 2; g.weightx = 0; g.gridwidth = 3;
        panel.add(btnLimpiarBusqueda, g);

        return panel;
    }

    /* -------------------------------------------------------------
       PANEL "RESULTADOS" (ejercicios 3 y 5)
       ------------------------------------------------------------- */

    private JPanel construirPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Resultados"));

        // Se sobreescribe isCellEditable para que la tabla sea de solo lectura:
        // la edición se hace únicamente desde el formulario de "Datos".
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
            // Solo reaccionar cuando la selección ya quedó establecida.
            if (!evento.getValueIsAdjusting()) {
                cargarFormularioDesdeTabla();
            }
        });

        JScrollPane scroll = new JScrollPane(tblAlumnos);
        scroll.setPreferredSize(new Dimension(680, 380));

        // Etiquetas con las estadísticas calculadas por el DAO.
        lblCantidad = new JLabel("Alumnos registrados: -");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 12));

        lblPromedio = new JLabel("Edad promedio: -");
        lblPromedio.setFont(new Font("Arial", Font.BOLD, 12));

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(0x555555));

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 2));
        pie.add(lblCantidad);
        pie.add(lblPromedio);
        pie.add(lblEstado);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    /** Pie de la ventana con un recordatorio de uso. */
    private void construirPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 6, 12));

        JLabel texto = new JLabel(
                "Seleccioná un alumno de la tabla para cargarlo en el formulario y poder modificarlo o eliminarlo.");
        texto.setFont(new Font("Arial", Font.ITALIC, 11));
        texto.setForeground(new Color(0x666666));

        panel.add(texto);
        add(panel, BorderLayout.SOUTH);
    }

    /* =============================================================
       CARGA DE DATOS
       ============================================================= */

    /** Llena el JComboBox del formulario y del filtro con los cursos. */
    private void cargarCursos() {
        try {
            List<Curso> cursos = cursoDAO.listar();

            // Cada combo usa SU PROPIO modelo. Compartir el mismo modelo
            // haría que "(Todos los cursos)" también aparezca en el
            // formulario, que solo debe ofrecer cursos reales.
            DefaultComboBoxModel<Curso> modeloFormulario = new DefaultComboBoxModel<>();
            DefaultComboBoxModel<Curso> modeloFiltro      = new DefaultComboBoxModel<>();

            // El filtro arranca con la opción "no filtrar".
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

    /* =============================================================
       BÚSQUEDAS (ejercicios 1 y 2)
       ============================================================= */

    /** Búsqueda por texto: nombre, apellido o email (LIKE %texto%). */
    private void buscarPorTexto() {
        String texto = txtBuscar.getText().trim();

        try {
            if (texto.isEmpty()) {
                cargarAlumnos();
                lblEstado.setText("Mostrando todos los alumnos");
                return;
            }
            List<Alumno> alumnos = alumnoDAO.buscar(texto);
            llenarTabla(alumnos);
            lblEstado.setText("Búsqueda por texto: \"" + texto + "\"");

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Ejercicio 1: busca los alumnos del curso elegido en el JComboBox.
     * Si se eligió "(Todos los cursos)" muestra todos los alumnos.
     */
    private void buscarPorCurso() {
        Curso filtro = (Curso) cmbFiltroCurso.getSelectedItem();

        try {
            if (filtro == null || filtro.getId() == 0) {
                cargarAlumnos();
                lblEstado.setText("Mostrando todos los alumnos");
                return;
            }

            List<Alumno> alumnos = alumnoDAO.buscarPorCurso(filtro.getNombre());
            llenarTabla(alumnos);
            lblEstado.setText("Curso: " + filtro.getNombre());

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Ejercicio 2: limpia los criterios de búsqueda y vuelve a mostrar
     * todos los alumnos.
     */
    private void limpiarBusqueda() {
        txtBuscar.setText("");
        if (cmbFiltroCurso.getItemCount() > 0) {
            cmbFiltroCurso.setSelectedIndex(0);
        }
        cargarAlumnos();
        lblEstado.setText("Búsqueda limpiada: se muestran todos los alumnos");
    }

    /* =============================================================
       ESTADÍSTICAS (ejercicios 3 y 4)
       ============================================================= */

    /**
     * Pide al DAO las estadísticas de la tabla alumnos (COUNT y AVG)
     * y las muestra en el pie del panel "Resultados".
     */
    private void actualizarEstadisticas() {
        try {
            int total = alumnoDAO.cantidadAlumnos();
            double promedio = alumnoDAO.promedioEdad();

            lblCantidad.setText("Alumnos registrados: " + total);

            String promTexto = (total == 0)
                    ? "-"
                    : String.valueOf(Math.round(promedio * 10.0) / 10.0);

            lblPromedio.setText("Edad promedio: " + promTexto + " años");

        } catch (SQLException e) {
            lblCantidad.setText("Alumnos registrados: -");
            lblPromedio.setText("Edad promedio: -");
            mostrarError(e);
        }
    }

    /**
     * Ejercicio 4: muestra la edad promedio de los alumnos.
     * La cuenta la hace la base de datos con AVG(edad).
     */
    private void mostrarPromedioEdad() {
        try {
            int total = alumnoDAO.cantidadAlumnos();
            double promedio = alumnoDAO.promedioEdad();

            if (total == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay alumnos cargados, no se puede calcular el promedio.",
                        "Promedio de edad", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String promTexto = String.valueOf(Math.round(promedio * 10.0) / 10.0);

            lblPromedio.setText("Edad promedio: " + promTexto + " años");

            JOptionPane.showMessageDialog(this,
                    "Cantidad de alumnos: " + total + "\n"
                  + "Edad promedio (AVG(edad)): " + promTexto + " años",
                    "Promedio de edad", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /** Vuelca la lista de alumnos en el modelo de la tabla. */
    private void llenarTabla(List<Alumno> alumnos) {
        modeloTabla.setRowCount(0);   // vacía la tabla

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
       ACCIONES DEL CRUD
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
            // Aviso previo de email duplicado, para un mensaje más claro.
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
            cargarAlumnos();

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
            cargarAlumnos();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /** Elimina el alumno seleccionado, previa confirmación. */
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
            cargarAlumnos();

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /* =============================================================
       FORMULARIO
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

    /** Vacía el formulario y deja el modo "alta". */
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

        alumno.setCursoId(curso.getId());    // clave foránea curso_id
        return alumno;
    }

    /**
     * Revisa los datos del formulario ANTES de mandarlos a la base.
     *
     * @return el mensaje con el error, o null si todo está bien.
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
       MENSAJES AL USUARIO
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
     * Nunca se muestra el detalle técnico al usuario final.
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
