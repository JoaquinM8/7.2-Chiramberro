package modelo;

import java.math.BigDecimal;

/**
 * POJO que representa una fila de la tabla {@code presupuestos}.
 *
 * <p>Un presupuesto es un plan: fija un monto maximo para una categoria
 * durante un mes. El gasto real nunca se guarda aqui; se calcula a partir de
 * la tabla {@code movimientos}.</p>
 */
public class Presupuesto {

    /* =============================================================
       ATRIBUTOS
       ============================================================= */
    private int idPresupuesto;
    private int idUsuario;
    private int idCategoria;
    private int anio;
    private int mes;
    private BigDecimal montoPlanificado;
    private String observaciones;

    /* =============================================================
       CONSTRUCTORES
       ============================================================= */

    /** Constructor vacio exigido por el patron POJO/JavaBeans. */
    public Presupuesto() {
    }

    /** Constructor sin id, pensado para altas (AUTO_INCREMENT). */
    public Presupuesto(int idUsuario, int idCategoria, int anio, int mes,
                       BigDecimal montoPlanificado, String observaciones) {
        this(0, idUsuario, idCategoria, anio, mes, montoPlanificado, observaciones);
    }

    /** Constructor completo, usado al reconstruir filas del ResultSet. */
    public Presupuesto(int idPresupuesto, int idUsuario, int idCategoria,
                       int anio, int mes, BigDecimal montoPlanificado,
                       String observaciones) {
        this.idPresupuesto = idPresupuesto;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.anio = anio;
        this.mes = mes;
        this.montoPlanificado = montoPlanificado;
        this.observaciones = observaciones;
    }

    /* =============================================================
       GETTERS Y SETTERS
       ============================================================= */
    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public BigDecimal getMontoPlanificado() {
        return montoPlanificado;
    }

    public void setMontoPlanificado(BigDecimal montoPlanificado) {
        this.montoPlanificado = montoPlanificado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */
    @Override
    public String toString() {
        return "Presupuesto{"
                + "idPresupuesto=" + idPresupuesto
                + ", idUsuario=" + idUsuario
                + ", idCategoria=" + idCategoria
                + ", anio=" + anio
                + ", mes=" + mes
                + ", montoPlanificado=" + montoPlanificado
                + ", observaciones='" + observaciones + '\''
                + '}';
    }
}
