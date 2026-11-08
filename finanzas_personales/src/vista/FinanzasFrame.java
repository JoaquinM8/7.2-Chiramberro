package vista;

import dao.MovimientoDAO;
import dao.PresupuestoDAO;
import modelo.Movimiento;
import modelo.ResumenMensual;
import servicio.FinanzasServicio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Ventana principal de la Entrega 4.
 *
 * <p>La vista captura datos, muestra tablas y mensajes, y delega todas las
 * operaciones en los DAO. No contiene SQL: las cuentas, categorias, usuarios y
 * presupuestos se obtienen mediante metodos de acceso a datos.</p>
 *
 * <p>La clase esta organizada en tres pestanas: movimientos, resumen mensual y
 * presupuestos. Los eventos de los botones llaman a los metodos privados que
 * implementan cada operacion.</p>
 */
public class FinanzasFrame extends JFrame {

    /* =============================================================
       1) DEPENDENCIAS Y ESTADO
       ============================================================= */

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final PresupuestoDAO presupuestoDAO = new PresupuestoDAO();
    private final FinanzasServicio servicio = new FinanzasServicio();

    /** Evita dialogos modales durante la generacion automatica de capturas. */
    private final boolean modoCaptura = Boolean.getBoolean("finanzas.captura");

    private final JTabbedPane tabs = new JTabbedPane();

    private final List<Movimiento> movimientosActuales = new ArrayList<>();
    private final Map<Integer, String> nombresCuentas = new HashMap<>();
    private final Map<Integer, String> nombresCategorias = new HashMap<>();

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;

    /* =============================================================
       2) COMPONENTES DEL FORMULARIO DE MOVIMIENTOS
       ============================================================= */

    private final JTextField txtId = new JTextField(8);
    private final JTextField txtFecha = new JTextField(12);
    private final JComboBox<String> cmbTipo =
            new JComboBox<>(new String[]{"INGRESO", "GASTO"});
    private final JComboBox<OpcionCombo> cmbCuenta = new JComboBox<>();
    private final JComboBox<OpcionCombo> cmbCategoria = new JComboBox<>();
    private final JTextField txtImporte = new JTextField(12);
    private final JTextField txtMedio = new JTextField(14);
    private final JTextField txtDescripcion = new JTextField(28);
    private final JCheckBox chkRecurrente = new JCheckBox("Recurrente");

    private final JComboBox<String> cmbFiltroTipo =
            new JComboBox<>(new String[]{"TODOS", "INGRESO", "GASTO"});
    private final JTextField txtDesde = new JTextField(10);
    private final JTextField txtHasta = new JTextField(10);
    private final JTextField txtTexto = new JTextField(14);

    private final DefaultTableModel modeloMovimientos =
            new DefaultTableModel(new Object[]{
                "ID", "Fecha", "Tipo", "Cuenta", "Categoria",
                "Descripcion", "Importe", "Medio", "Recurrente"
            }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable tabla = new JTable(modeloMovimientos);

    /* =============================================================
       3) COMPONENTES DEL RESUMEN
       ============================================================= */

    private final JLabel lblIngresos = new JLabel("Ingresos: --");
    private final JLabel lblGastos = new JLabel("Gastos: --");
    private final JLabel lblResultado = new JLabel("Resultado: --");
    private final JLabel lblTasa = new JLabel("Tasa de ahorro: --");

    private final JSpinner spnResumenMes = new JSpinner(
            new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
    private final JSpinner spnResumenAnio = new JSpinner(
            new SpinnerNumberModel(LocalDate.now().getYear(), 1900, 2200, 1));

    /* =============================================================
       4) COMPONENTES DE PRESUPUESTOS
       ============================================================= */

    private final JComboBox<OpcionCombo> cmbUsuarioPres = new JComboBox<>();
    private final JComboBox<OpcionCombo> cmbCategoriaPres = new JComboBox<>();
    private final JSpinner spnPresMes = new JSpinner(
            new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
    private final JSpinner spnPresAnio = new JSpinner(
            new SpinnerNumberModel(LocalDate.now().getYear(), 1900, 2200, 1));
    private final JTextField txtMontoPres = new JTextField(12);
    private final JTextField txtObsPres = new JTextField(22);

    private final DefaultTableModel modeloPresupuestos =
            new DefaultTableModel(new Object[]{
                "ID", "Categoria", "Planificado", "Gasto real",
                "Disponible", "Ejecucion %", "Estado"
            }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable tablaPresupuestos = new JTable(modeloPresupuestos);

    /* =============================================================
       5) CONSTRUCTOR
       ============================================================= */

    public FinanzasFrame() {
        super("Finanzas Personales Inteligentes - Entrega 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        tabs.addTab("Movimientos", crearPanelMovimientos());
        tabs.addTab("Resumen mensual", crearPanelResumen());
        tabs.addTab("Presupuestos", crearPanelPresupuestos());
        add(tabs, BorderLayout.CENTER);

        conectarEventos();

        try {
            cargarCombos();
            cargarTabla();
            ajustarPeriodoInicial();
            actualizarResumen();
            cargarPresupuestos();
            limpiarFormulario();
        } catch (Exception ex) {
            mostrarError(ex);
        }

        // Se conectan despues de la carga inicial para que los cambios de
        // periodo o de usuario actualicen la vista sin disparar consultas
        // mientras los combos todavia se estan llenando.
        conectarEventosDependientes();
    }

    /* =============================================================
       6) CONSTRUCCION DE PANELES
       ============================================================= */

    private JPanel crearPanelMovimientos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.anchor = GridBagConstraints.WEST;

        int fila = 0;
        agregarCampo(formulario, g, fila++, "ID (solo lectura)", txtId);
        txtId.setEditable(false);
        agregarCampo(formulario, g, fila++, "Fecha (AAAA-MM-DD)", txtFecha);
        agregarCampo(formulario, g, fila++, "Tipo", cmbTipo);
        agregarCampo(formulario, g, fila++, "Cuenta", cmbCuenta);
        agregarCampo(formulario, g, fila++, "Categoria", cmbCategoria);
        agregarCampo(formulario, g, fila++, "Importe", txtImporte);
        agregarCampo(formulario, g, fila++, "Medio de pago", txtMedio);
        agregarCampo(formulario, g, fila++, "Descripcion", txtDescripcion);

        g.gridx = 1;
        g.gridy = fila;
        g.weightx = 1;
        g.fill = GridBagConstraints.NONE;
        formulario.add(chkRecurrente, g);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel superior = new JPanel(new BorderLayout(4, 4));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnFiltrar = new JButton("Aplicar filtros");
        JButton btnTodos = new JButton("Mostrar todos");
        filtros.add(new JLabel("Tipo:"));
        filtros.add(cmbFiltroTipo);
        filtros.add(new JLabel("Desde:"));
        filtros.add(txtDesde);
        filtros.add(new JLabel("Hasta (exclusiva):"));
        filtros.add(txtHasta);
        filtros.add(new JLabel("Descripcion:"));
        filtros.add(txtTexto);
        filtros.add(btnFiltrar);
        filtros.add(btnTodos);

        JPanel centro = new JPanel(new BorderLayout(4, 4));
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);

        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(75);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(105);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(8).setPreferredWidth(80);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnFiltrar.addActionListener(e -> filtrar());
        btnTodos.addActionListener(e -> ejecutarSeguro(() -> {
            limpiarFiltros();
            cargarTabla();
        }));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                seleccionarMovimiento();
            }
        });

        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel periodo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodo.add(new JLabel("Mes:"));
        periodo.add(spnResumenMes);
        periodo.add(new JLabel("Anio:"));
        periodo.add(spnResumenAnio);
        JButton btnActualizar = new JButton("Actualizar resumen");
        btnActualizar.addActionListener(e -> actualizarResumen());
        periodo.add(btnActualizar);

        JPanel indicadores = new JPanel(new GridLayout(0, 1, 8, 8));
        indicadores.setBorder(BorderFactory.createTitledBorder("Indicadores del periodo"));
        lblIngresos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGastos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTasa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        indicadores.add(lblIngresos);
        indicadores.add(lblGastos);
        indicadores.add(lblResultado);
        indicadores.add(lblTasa);

        JLabel nota = new JLabel(
                "<html>La tasa de ahorro se calcula como "
                + "(ingresos - gastos) / ingresos x 100. "
                + "Si no hay ingresos, el indicador no esta disponible.</html>");

        panel.add(periodo, BorderLayout.NORTH);
        panel.add(indicadores, BorderLayout.CENTER);
        panel.add(nota, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPresupuestos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.anchor = GridBagConstraints.WEST;

        int fila = 0;
        agregarCampo(formulario, g, fila++, "Usuario", cmbUsuarioPres);
        agregarCampo(formulario, g, fila++, "Anio", spnPresAnio);
        agregarCampo(formulario, g, fila++, "Mes", spnPresMes);
        agregarCampo(formulario, g, fila++, "Categoria", cmbCategoriaPres);
        agregarCampo(formulario, g, fila++, "Monto planificado", txtMontoPres);
        agregarCampo(formulario, g, fila++, "Observaciones", txtObsPres);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardarPres = new JButton("Guardar presupuesto");
        JButton btnVerPres = new JButton("Comparar gasto real");
        botones.add(btnGuardarPres);
        botones.add(btnVerPres);

        JPanel superior = new JPanel(new BorderLayout(4, 4));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPresupuestos), BorderLayout.CENTER);
        panel.add(new JLabel("El gasto real se calcula desde los movimientos; "
                + "no se edita en la tabla."), BorderLayout.SOUTH);

        tablaPresupuestos.setFillsViewportHeight(true);
        tablaPresupuestos.setRowHeight(22);
        tablaPresupuestos.getColumnModel().getColumn(0).setPreferredWidth(45);
        tablaPresupuestos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaPresupuestos.getColumnModel().getColumn(2).setPreferredWidth(115);
        tablaPresupuestos.getColumnModel().getColumn(3).setPreferredWidth(115);
        tablaPresupuestos.getColumnModel().getColumn(4).setPreferredWidth(115);
        tablaPresupuestos.getColumnModel().getColumn(5).setPreferredWidth(95);
        tablaPresupuestos.getColumnModel().getColumn(6).setPreferredWidth(170);

        tablaPresupuestos.getColumnModel().getColumn(6)
                .setCellRenderer(new EstadoPresupuestoRenderer());

        btnGuardarPres.addActionListener(e -> guardarPresupuesto());
        btnVerPres.addActionListener(e -> cargarPresupuestos());

        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints g, int fila,
                              String etiqueta, Component campo) {
        g.gridx = 0;
        g.gridy = fila;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, g);
        g.fill = GridBagConstraints.NONE;
    }

    /* =============================================================
       7) EVENTOS GENERALES
       ============================================================= */

    private void conectarEventos() {
        // Al cambiar el tipo se recargan solo las categorias compatibles.
        cmbTipo.addActionListener(e -> ejecutarSeguro(this::cargarCategorias));
    }

    private void conectarEventosDependientes() {
        // El resumen se actualiza al cambiar el mes o el anio.
        spnResumenMes.addChangeListener(e -> actualizarResumen());
        spnResumenAnio.addChangeListener(e -> actualizarResumen());

        // La comparacion de presupuestos se actualiza al cambiar usuario o
        // periodo. Los botones quedan como refresco manual.
        cmbUsuarioPres.addActionListener(e -> cargarPresupuestos());
        spnPresMes.addChangeListener(e -> cargarPresupuestos());
        spnPresAnio.addChangeListener(e -> cargarPresupuestos());
    }

    private void ejecutarSeguro(Accion accion) {
        try {
            accion.ejecutar();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws Exception;
    }

    /* =============================================================
       8) CARGA DE DATOS Y COMBOS
       ============================================================= */

    private void cargarCombos() throws SQLException {
        cargarCuentas();
        cargarMapaCategorias();
        cargarCategorias();
        cargarUsuariosPresupuesto();
        cargarCategoriasPresupuesto();
    }

    private void cargarCuentas() throws SQLException {
        Integer seleccionada = getIdSeleccionado(cmbCuenta);
        cmbCuenta.removeAllItems();
        nombresCuentas.clear();

        for (Object[] fila : movimientoDAO.listarCuentasActivas()) {
            int id = ((Number) fila[0]).intValue();
            String nombre = (String) fila[1];
            nombresCuentas.put(id, nombre);
            cmbCuenta.addItem(new OpcionCombo(id, nombre));
        }

        if (seleccionada != null) {
            seleccionarComboPorId(cmbCuenta, seleccionada);
        }
    }

    private void cargarMapaCategorias() throws SQLException {
        nombresCategorias.clear();

        for (Object[] fila : movimientoDAO.listarCategorias()) {
            nombresCategorias.put(
                    ((Number) fila[0]).intValue(), (String) fila[1]);
        }
    }

    private void cargarCategorias() throws SQLException {
        Integer seleccionada = getIdSeleccionado(cmbCategoria);
        cmbCategoria.removeAllItems();

        String tipo = (String) cmbTipo.getSelectedItem();
        if (tipo == null) {
            return;
        }

        for (Object[] fila : movimientoDAO.listarCategoriasPorTipo(tipo)) {
            cmbCategoria.addItem(new OpcionCombo(
                    ((Number) fila[0]).intValue(), (String) fila[1]));
        }

        if (seleccionada != null) {
            seleccionarComboPorId(cmbCategoria, seleccionada);
        }
    }

    private void cargarUsuariosPresupuesto() throws SQLException {
        Integer seleccionado = getIdSeleccionado(cmbUsuarioPres);
        cmbUsuarioPres.removeAllItems();

        for (Object[] fila : presupuestoDAO.listarUsuarios()) {
            cmbUsuarioPres.addItem(new OpcionCombo(
                    ((Number) fila[0]).intValue(), (String) fila[1]));
        }

        if (seleccionado != null) {
            seleccionarComboPorId(cmbUsuarioPres, seleccionado);
        }
    }

    private void cargarCategoriasPresupuesto() throws SQLException {
        Integer seleccionada = getIdSeleccionado(cmbCategoriaPres);
        cmbCategoriaPres.removeAllItems();

        for (Object[] fila : presupuestoDAO.listarCategoriasPresupuestables()) {
            cmbCategoriaPres.addItem(new OpcionCombo(
                    ((Number) fila[0]).intValue(), (String) fila[1]));
        }

        if (seleccionada != null) {
            seleccionarComboPorId(cmbCategoriaPres, seleccionada);
        }
    }

    private void cargarTabla() throws SQLException {
        mostrarMovimientos(movimientoDAO.listarTodos());
    }

    /**
     * Al abrir la aplicacion, muestra el ultimo mes que tiene movimientos.
     * Asi el resumen y los presupuestos no arrancan vacios cuando los datos de
     * ejemplo pertenecen a otro mes. Si no hay movimientos, se conserva el mes
     * actual.
     */
    private void ajustarPeriodoInicial() {
        LocalDate ultima = null;

        for (Movimiento m : movimientosActuales) {
            if (m.getFecha() != null
                    && (ultima == null || m.getFecha().isAfter(ultima))) {
                ultima = m.getFecha();
            }
        }

        if (ultima != null) {
            spnResumenAnio.setValue(ultima.getYear());
            spnResumenMes.setValue(ultima.getMonthValue());
            spnPresAnio.setValue(ultima.getYear());
            spnPresMes.setValue(ultima.getMonthValue());
        }
    }

    private void mostrarMovimientos(List<Movimiento> lista) {
        modeloMovimientos.setRowCount(0);
        movimientosActuales.clear();
        movimientosActuales.addAll(lista);

        for (Movimiento m : lista) {
            modeloMovimientos.addRow(new Object[]{
                m.getIdMovimiento(),
                m.getFecha(),
                m.getTipo(),
                nombreCuenta(m.getIdCuenta()),
                nombreCategoria(m.getIdCategoria()),
                m.getDescripcion(),
                formatearMoneda(m.getImporte()),
                m.getMedioPago(),
                m.isRecurrente() ? "SI" : "NO"
            });
        }
    }

    /* =============================================================
       9) SELECCION Y FORMULARIO
       ============================================================= */

    private void seleccionarMovimiento() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= movimientosActuales.size()) {
            return;
        }

        Movimiento m = movimientosActuales.get(fila);

        txtId.setText(String.valueOf(m.getIdMovimiento()));
        txtFecha.setText(m.getFecha() == null ? "" : m.getFecha().toString());
        cmbTipo.setSelectedItem(m.getTipo());
        seleccionarComboPorId(cmbCuenta, m.getIdCuenta());
        seleccionarComboPorId(cmbCategoria, m.getIdCategoria());
        txtImporte.setText(m.getImporte() == null
                ? "" : m.getImporte().toPlainString());
        txtMedio.setText(m.getMedioPago());
        txtDescripcion.setText(m.getDescripcion());
        chkRecurrente.setSelected(m.isRecurrente());
    }

    private Movimiento leerFormulario() {
        String fechaTexto = txtFecha.getText().trim();
        String importeTexto = txtImporte.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String medio = txtMedio.getText().trim();

        if (esBlanco(fechaTexto) || esBlanco(importeTexto)
                || esBlanco(descripcion) || esBlanco(medio)) {
            throw new IllegalArgumentException(
                    "Completa fecha, importe, descripcion y medio de pago.");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto, FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Fecha invalida. Usa el formato AAAA-MM-DD.", ex);
        }

        BigDecimal importe;
        try {
            importe = new BigDecimal(importeTexto.replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Importe invalido.", ex);
        }

        if (importe.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor que cero.");
        }

        OpcionCombo cuenta = (OpcionCombo) cmbCuenta.getSelectedItem();
        OpcionCombo categoria = (OpcionCombo) cmbCategoria.getSelectedItem();
        if (cuenta == null || categoria == null) {
            throw new IllegalArgumentException(
                    "Selecciona una cuenta y una categoria.");
        }

        Movimiento m = new Movimiento(
                cuenta.getId(),
                categoria.getId(),
                (String) cmbTipo.getSelectedItem(),
                fecha,
                importe,
                descripcion,
                medio,
                chkRecurrente.isSelected());

        if (!esBlanco(txtId.getText())) {
            try {
                m.setIdMovimiento(Integer.parseInt(txtId.getText().trim()));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("ID de movimiento invalido.", ex);
            }
        }
        return m;
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtFecha.setText(LocalDate.now().toString());
        cmbTipo.setSelectedItem("GASTO");
        txtImporte.setText("");
        txtMedio.setText("");
        txtDescripcion.setText("");
        chkRecurrente.setSelected(false);
        tabla.clearSelection();
    }

    private void limpiarFiltros() {
        cmbFiltroTipo.setSelectedIndex(0);
        txtDesde.setText("");
        txtHasta.setText("");
        txtTexto.setText("");
    }

    /* =============================================================
       10) CRUD DE MOVIMIENTOS
       ============================================================= */

    private void guardar() {
        ejecutarSeguro(() -> {
            Movimiento m = leerFormulario();

            if (movimientoDAO.insertar(m)) {
                mostrarInfo("Movimiento guardado con ID " + m.getIdMovimiento() + ".");
                limpiarFormulario();
                cargarTabla();
                actualizarResumen();
                cargarPresupuestos();
            } else {
                mostrarInfo("No se inserto el movimiento.");
            }
        });
    }

    private void modificar() {
        ejecutarSeguro(() -> {
            if (esBlanco(txtId.getText())) {
                throw new IllegalArgumentException(
                        "Selecciona un movimiento de la tabla.");
            }

            Movimiento m = leerFormulario();
            if (movimientoDAO.actualizar(m)) {
                mostrarInfo("Movimiento actualizado.");
                limpiarFormulario();
                cargarTabla();
                actualizarResumen();
                cargarPresupuestos();
            } else {
                mostrarInfo("No se encontro el movimiento.");
            }
        });
    }

    private void eliminar() {
        ejecutarSeguro(() -> {
            if (esBlanco(txtId.getText())) {
                throw new IllegalArgumentException("Selecciona un movimiento.");
            }

            int id = Integer.parseInt(txtId.getText().trim());
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "Eliminar el movimiento " + id + "?",
                    "Confirmar eliminacion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (opcion == JOptionPane.YES_OPTION) {
                if (movimientoDAO.eliminar(id)) {
                    mostrarInfo("Movimiento eliminado.");
                    limpiarFormulario();
                    cargarTabla();
                    actualizarResumen();
                    cargarPresupuestos();
                } else {
                    mostrarInfo("No se encontro el movimiento.");
                }
            }
        });
    }

    /* =============================================================
       11) FILTROS
       ============================================================= */

    private void filtrar() {
        ejecutarSeguro(() -> {
            String texto = txtTexto.getText().trim();
            String tipo = (String) cmbFiltroTipo.getSelectedItem();

            LocalDate desde = esBlanco(txtDesde.getText())
                    ? LocalDate.of(1900, 1, 1)
                    : parseFecha(txtDesde.getText());
            LocalDate hasta = esBlanco(txtHasta.getText())
                    ? LocalDate.now().plusDays(1)
                    : parseFecha(txtHasta.getText());

            if (!desde.isBefore(hasta)) {
                throw new IllegalArgumentException(
                        "La fecha Desde debe ser anterior a Hasta.");
            }

            List<Movimiento> lista;

            if (!texto.isEmpty()) {
                lista = movimientoDAO.buscarPorDescripcion(texto);
                lista.removeIf(m -> m.getFecha().isBefore(desde)
                        || !m.getFecha().isBefore(hasta)
                        || (!"TODOS".equals(tipo)
                            && !m.getTipo().equals(tipo)));
            } else if (!"TODOS".equals(tipo)) {
                lista = movimientoDAO.listarPorTipoYFechas(tipo, desde, hasta);
            } else {
                lista = movimientoDAO.listarTodos();
                lista.removeIf(m -> m.getFecha().isBefore(desde)
                        || !m.getFecha().isBefore(hasta));
            }

            mostrarMovimientos(lista);
        });
    }

    private LocalDate parseFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Fecha invalida: '" + texto + "'. Usa AAAA-MM-DD.", ex);
        }
    }

    /* =============================================================
       12) RESUMEN MENSUAL
       ============================================================= */

    private void actualizarResumen() {
        ejecutarSeguro(() -> {
            int anio = valorEntero(spnResumenAnio);
            int mes = valorEntero(spnResumenMes);

            LocalDate desde = LocalDate.of(anio, mes, 1);
            LocalDate hasta = desde.plusMonths(1);
            ResumenMensual resumen = movimientoDAO.resumenMensual(desde, hasta);

            lblIngresos.setText("Ingresos: " + formatearMoneda(resumen.getIngresos()));
            lblGastos.setText("Gastos: " + formatearMoneda(resumen.getGastos()));
            lblResultado.setText("Resultado: " + formatearMoneda(resumen.getResultado()));

            try {
                BigDecimal tasa = servicio.calcularTasaAhorro(resumen);
                lblTasa.setText("Tasa de ahorro: " + tasa.toPlainString() + " %");
            } catch (IllegalArgumentException ex) {
                lblTasa.setText("Tasa de ahorro: no disponible (sin ingresos)");
            }

            lblResultado.setForeground(resumen.getResultado().signum() < 0
                    ? new Color(170, 0, 0)
                    : new Color(0, 110, 0));
        });
    }

    /* =============================================================
       13) PRESUPUESTOS
       ============================================================= */

    private void guardarPresupuesto() {
        ejecutarSeguro(() -> {
            OpcionCombo usuario = (OpcionCombo) cmbUsuarioPres.getSelectedItem();
            OpcionCombo categoria = (OpcionCombo) cmbCategoriaPres.getSelectedItem();
            if (usuario == null || categoria == null) {
                throw new IllegalArgumentException(
                        "Selecciona un usuario y una categoria.");
            }

            int anio = valorEntero(spnPresAnio);
            int mes = valorEntero(spnPresMes);

            String montoTexto = txtMontoPres.getText().trim();
            if (esBlanco(montoTexto)) {
                throw new IllegalArgumentException("Ingresa el monto planificado.");
            }

            BigDecimal monto;
            try {
                monto = new BigDecimal(montoTexto.replace(",", "."));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Monto invalido.", ex);
            }

            if (monto.signum() < 0) {
                throw new IllegalArgumentException(
                        "El monto planificado no puede ser negativo.");
            }

            boolean guardado = presupuestoDAO.guardar(
                    usuario.getId(),
                    categoria.getId(),
                    anio,
                    mes,
                    monto,
                    txtObsPres.getText().trim());

            if (guardado) {
                mostrarInfo("Presupuesto guardado.");
                cargarPresupuestos();
            } else {
                mostrarInfo("No se pudo guardar el presupuesto.");
            }
        });
    }

    private void cargarPresupuestos() {
        ejecutarSeguro(() -> {
            OpcionCombo usuario = (OpcionCombo) cmbUsuarioPres.getSelectedItem();
            if (usuario == null) {
                modeloPresupuestos.setRowCount(0);
                return;
            }

            int anio = valorEntero(spnPresAnio);
            int mes = valorEntero(spnPresMes);

            modeloPresupuestos.setRowCount(0);

            for (Object[] fila : presupuestoDAO.comparar(
                    usuario.getId(), anio, mes)) {

                BigDecimal planificado = (BigDecimal) fila[2];
                BigDecimal gastoReal = (BigDecimal) fila[3];
                BigDecimal disponible = (BigDecimal) fila[4];
                BigDecimal ejecucion = (BigDecimal) fila[5];

                modeloPresupuestos.addRow(new Object[]{
                    fila[0],
                    fila[1],
                    formatearMoneda(planificado),
                    formatearMoneda(gastoReal),
                    formatearMoneda(disponible),
                    ejecucion.toPlainString() + " %",
                    fila[6]
                });
            }
        });
    }

    /* =============================================================
       14) APOYO PARA CAPTURAS Y UTILIDADES
       ============================================================= */

    /** Selecciona una pestana por indice; se usa en la captura automatica. */
    public void seleccionarPestana(int indice) {
        tabs.setSelectedIndex(indice);
    }

    /** Configura el periodo del resumen y lo recalcula. */
    public void configurarResumen(int anio, int mes) {
        spnResumenAnio.setValue(anio);
        spnResumenMes.setValue(mes);
        actualizarResumen();
    }

    /** Configura usuario y periodo de presupuestos y actualiza la tabla. */
    public void configurarPresupuestos(int idUsuario, int anio, int mes) {
        seleccionarComboPorId(cmbUsuarioPres, idUsuario);
        spnPresAnio.setValue(anio);
        spnPresMes.setValue(mes);
        cargarPresupuestos();
    }

    /** Deja seleccionada la primera fila para mostrar el formulario completo. */
    public void seleccionarPrimeraFilaMovimientos() {
        if (modeloMovimientos.getRowCount() > 0) {
            tabla.setRowSelectionInterval(0, 0);
        }
    }

    private String nombreCuenta(int id) {
        String nombre = nombresCuentas.get(id);
        return nombre == null ? "ID " + id : nombre;
    }

    private String nombreCategoria(int id) {
        String nombre = nombresCategorias.get(id);
        return nombre == null ? "ID " + id : nombre;
    }

    private String formatearMoneda(BigDecimal valor) {
        BigDecimal importe = valor == null ? BigDecimal.ZERO : valor;
        NumberFormat formato = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-AR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return formato.format(importe);
    }

    private int valorEntero(JSpinner spinner) {
        return ((Number) spinner.getValue()).intValue();
    }

    private void seleccionarComboPorId(JComboBox<OpcionCombo> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            OpcionCombo opcion = combo.getItemAt(i);
            if (opcion.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private Integer getIdSeleccionado(JComboBox<OpcionCombo> combo) {
        OpcionCombo opcion = (OpcionCombo) combo.getSelectedItem();
        return opcion == null ? null : opcion.getId();
    }

    private boolean esBlanco(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private void mostrarInfo(String mensaje) {
        if (modoCaptura) {
            System.out.println("[INFO GUI] " + mensaje);
        } else {
            JOptionPane.showMessageDialog(this, mensaje,
                    "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarError(Exception ex) {
        ex.printStackTrace();
        String mensaje = ex.getMessage() == null ? ex.toString() : ex.getMessage();

        if (modoCaptura) {
            System.err.println("[ERROR GUI] " + mensaje);
        } else {
            JOptionPane.showMessageDialog(this, mensaje,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* =============================================================
       15) RENDERER DE ESTADO DE PRESUPUESTO
       ============================================================= */

    private static class EstadoPresupuestoRenderer extends DefaultTableCellRenderer {

        private static final Color ROJO = new Color(170, 0, 0);
        private static final Color AMARILLO = new Color(170, 110, 0);
        private static final Color VERDE = new Color(0, 110, 0);

        EstadoPresupuestoRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component componente = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String estado = value == null ? "" : value.toString();
            if (!isSelected) {
                if ("EXCEDIDO".equals(estado)) {
                    componente.setForeground(ROJO);
                } else if ("AL LIMITE".equals(estado)) {
                    componente.setForeground(AMARILLO);
                } else {
                    componente.setForeground(VERDE);
                }
            }
            return componente;
        }
    }
}
