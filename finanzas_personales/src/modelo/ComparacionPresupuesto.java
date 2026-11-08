package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Comparacion entre un presupuesto planificado y el gasto real del periodo.
 *
 * <p>El gasto real se calcula desde {@code movimientos}; el estado
 * (DENTRO DEL PRESUPUESTO, AL LIMITE o EXCEDIDO) se obtiene en la consulta
 * SQL y se transporta en este objeto para que la interfaz no repita la regla.</p>
 */
public class ComparacionPresupuesto {

    /* =============================================================
       ATRIBUTOS
       ============================================================= */
    private final int idPresupuesto;
    private final String usuario;
    private final String categoria;
    private final int anio;
    private final int mes;
    private final BigDecimal montoPlanificado;
    private final BigDecimal gastoReal;
    private final BigDecimal disponible;
    private final String estado;

    /* =============================================================
       CONSTRUCTOR
       ============================================================= */
    public ComparacionPresupuesto(int idPresupuesto, String usuario, String categoria,
                                  int anio, int mes, BigDecimal montoPlanificado,
                                  BigDecimal gastoReal, BigDecimal disponible, String estado) {
        this.idPresupuesto = idPresupuesto;
        this.usuario = usuario;
        this.categoria = categoria;
        this.anio = anio;
        this.mes = mes;
        this.montoPlanificado = montoPlanificado == null
                ? BigDecimal.ZERO : montoPlanificado;
        this.gastoReal = gastoReal == null ? BigDecimal.ZERO : gastoReal;
        this.disponible = disponible == null
                ? this.montoPlanificado.subtract(this.gastoReal) : disponible;
        this.estado = estado;
    }

    /* =============================================================
       GETTERS Y CALCULOS
       ============================================================= */
    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getAnio() {
        return anio;
    }

    public int getMes() {
        return mes;
    }

    public BigDecimal getMontoPlanificado() {
        return montoPlanificado;
    }

    public BigDecimal getGastoReal() {
        return gastoReal;
    }

    public BigDecimal getDisponible() {
        return disponible;
    }

    public String getEstado() {
        return estado;
    }

    /** Porcentaje ejecutado del presupuesto (gasto real / planificado * 100). */
    public BigDecimal getPorcentajeEjecutado() {
        if (montoPlanificado.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return gastoReal.multiply(new BigDecimal("100"))
                .divide(montoPlanificado, 2, RoundingMode.HALF_UP);
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */
    @Override
    public String toString() {
        return "ComparacionPresupuesto{"
                + "idPresupuesto=" + idPresupuesto
                + ", usuario='" + usuario + '\''
                + ", categoria='" + categoria + '\''
                + ", anio=" + anio
                + ", mes=" + mes
                + ", montoPlanificado=" + montoPlanificado
                + ", gastoReal=" + gastoReal
                + ", disponible=" + disponible
                + ", estado='" + estado + '\''
                + '}';
    }
}
