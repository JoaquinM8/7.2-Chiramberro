package modelo;

import java.math.BigDecimal;

/**
 * Resultado de una consulta de agregacion sobre {@code movimientos}.
 *
 * No es una tabla: es un objeto de transporte que guarda los totales que
 * calcula MySQL con {@code SUM(CASE WHEN ...)}. Los importes se mantienen
 * como {@link BigDecimal} para no perder precision.
 */
public class ResumenTotales {

    /* =============================================================
       ATRIBUTOS
       ============================================================= */
    private final long cantidadIngresos;
    private final long cantidadGastos;
    private final BigDecimal totalIngresos;
    private final BigDecimal totalGastos;

    /* =============================================================
       CONSTRUCTOR
       ============================================================= */

    /**
     * @param cantidadIngresos cantidad de filas de tipo INGRESO.
     * @param cantidadGastos   cantidad de filas de tipo GASTO.
     * @param totalIngresos    suma de los importes INGRESO (null se trata como 0).
     * @param totalGastos      suma de los importes GASTO (null se trata como 0).
     */
    public ResumenTotales(long cantidadIngresos, long cantidadGastos,
                          BigDecimal totalIngresos, BigDecimal totalGastos) {
        this.cantidadIngresos = cantidadIngresos;
        this.cantidadGastos = cantidadGastos;
        this.totalIngresos = totalIngresos == null ? BigDecimal.ZERO : totalIngresos;
        this.totalGastos = totalGastos == null ? BigDecimal.ZERO : totalGastos;
    }

    /* =============================================================
       GETTERS
       ============================================================= */

    public long getCantidadIngresos() {
        return cantidadIngresos;
    }

    public long getCantidadGastos() {
        return cantidadGastos;
    }

    public long getCantidadTotal() {
        return cantidadIngresos + cantidadGastos;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    /** Balance del periodo: ingresos menos gastos. */
    public BigDecimal getBalance() {
        return totalIngresos.subtract(totalGastos);
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */

    @Override
    public String toString() {
        return "ResumenTotales{"
                + "cantidadIngresos=" + cantidadIngresos
                + ", cantidadGastos=" + cantidadGastos
                + ", totalIngresos=" + totalIngresos
                + ", totalGastos=" + totalGastos
                + ", balance=" + getBalance()
                + '}';
    }
}
