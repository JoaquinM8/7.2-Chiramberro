package modelo;

import java.math.BigDecimal;

/**
 * Resultado del resumen de un periodo mensual.
 *
 * <p>No es una tabla: es un objeto de transporte con las tres cantidades que
 * calcula MySQL mediante {@code SUM(CASE WHEN ...)}. Se mantiene
 * {@link BigDecimal} para no perder precision con importes monetarios.</p>
 */
public class ResumenMensual {

    /* =============================================================
       ATRIBUTOS
       ============================================================= */
    private final BigDecimal ingresos;
    private final BigDecimal gastos;
    private final BigDecimal resultado;

    /* =============================================================
       CONSTRUCTOR
       ============================================================= */

    /**
     * @param ingresos  suma de los movimientos de tipo INGRESO.
     * @param gastos    suma de los movimientos de tipo GASTO.
     * @param resultado ingresos menos gastos (puede ser negativo).
     */
    public ResumenMensual(BigDecimal ingresos, BigDecimal gastos, BigDecimal resultado) {
        this.ingresos = ingresos == null ? BigDecimal.ZERO : ingresos;
        this.gastos = gastos == null ? BigDecimal.ZERO : gastos;
        this.resultado = resultado == null ? BigDecimal.ZERO : resultado;
    }

    /* =============================================================
       GETTERS
       ============================================================= */
    public BigDecimal getIngresos() {
        return ingresos;
    }

    public BigDecimal getGastos() {
        return gastos;
    }

    public BigDecimal getResultado() {
        return resultado;
    }

    /** Indica si el periodo no tuvo movimientos (o solo movimientos en cero). */
    public boolean estaVacio() {
        return ingresos.signum() == 0 && gastos.signum() == 0;
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */
    @Override
    public String toString() {
        return "ResumenMensual{"
                + "ingresos=" + ingresos
                + ", gastos=" + gastos
                + ", resultado=" + resultado
                + '}';
    }
}
