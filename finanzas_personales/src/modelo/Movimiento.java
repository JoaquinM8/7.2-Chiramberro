package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * POJO que representa una fila de la tabla {@code movimientos}.
 *
 * Es un objeto de transporte: no sabe conectarse a la base ni ejecutar SQL.
 * El importe se guarda como {@link BigDecimal} para no perder precision con
 * valores decimales (nunca double ni float para dinero).
 */
public class Movimiento {

    /* =============================================================
       ATRIBUTOS (mapean 1:1 con las columnas de la tabla)
       ============================================================= */
    private int idMovimiento;
    private int idCuenta;
    private int idCategoria;
    private String tipo;          // "INGRESO" o "GASTO"
    private LocalDate fecha;
    private BigDecimal importe;
    private String descripcion;
    private String medioPago;
    private boolean recurrente;   // columna es_recurrente (TINYINT 0/1)

    /* =============================================================
       CONSTRUCTORES
       ============================================================= */

    /** Constructor vacio exigido por el patron POJO/JavaBeans. */
    public Movimiento() {
    }

    /** Constructor completo, usado al reconstruir filas del ResultSet. */
    public Movimiento(int idMovimiento, int idCuenta, int idCategoria, String tipo,
                      LocalDate fecha, BigDecimal importe, String descripcion,
                      String medioPago, boolean recurrente) {
        this.idMovimiento = idMovimiento;
        this.idCuenta = idCuenta;
        this.idCategoria = idCategoria;
        this.tipo = tipo;
        this.fecha = fecha;
        this.importe = importe;
        this.descripcion = descripcion;
        this.medioPago = medioPago;
        this.recurrente = recurrente;
    }

    /**
     * Constructor sin id, pensado para altas: la base genera el
     * id_movimiento con AUTO_INCREMENT.
     */
    public Movimiento(int idCuenta, int idCategoria, String tipo, LocalDate fecha,
                      BigDecimal importe, String descripcion, String medioPago,
                      boolean recurrente) {
        this(0, idCuenta, idCategoria, tipo, fecha, importe, descripcion,
                medioPago, recurrente);
    }

    /* =============================================================
       GETTERS Y SETTERS
       ============================================================= */

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public boolean isRecurrente() {
        return recurrente;
    }

    public void setRecurrente(boolean recurrente) {
        this.recurrente = recurrente;
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */

    @Override
    public String toString() {
        return "Movimiento{"
                + "id=" + idMovimiento
                + ", idCuenta=" + idCuenta
                + ", idCategoria=" + idCategoria
                + ", tipo='" + tipo + '\''
                + ", fecha=" + fecha
                + ", importe=" + importe
                + ", descripcion='" + descripcion + '\''
                + ", medioPago='" + medioPago + '\''
                + ", recurrente=" + recurrente
                + '}';
    }
}
