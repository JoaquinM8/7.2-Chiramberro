package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ingresos y gastos agrupados por dia.
 *
 * <p>Es el resultado de una consulta {@code GROUP BY fecha}. El resultado
 * diario se calcula en Java como ingresos menos gastos para no repetir la
 * formula en el SQL.</p>
 */
public class ResumenDiario {

    /* =============================================================
       ATRIBUTOS
       ============================================================= */
    private final LocalDate fecha;
    private final BigDecimal ingresos;
    private final BigDecimal gastos;

    /* =============================================================
       CONSTRUCTOR
       ============================================================= */
    public ResumenDiario(LocalDate fecha, BigDecimal ingresos, BigDecimal gastos) {
        this.fecha = fecha;
        this.ingresos = ingresos == null ? BigDecimal.ZERO : ingresos;
        this.gastos = gastos == null ? BigDecimal.ZERO : gastos;
    }

    /* =============================================================
       GETTERS Y CALCULOS
       ============================================================= */
    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public BigDecimal getGastos() {
        return gastos;
    }

    public BigDecimal getResultado() {
        return ingresos.subtract(gastos);
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */
    @Override
    public String toString() {
        return "ResumenDiario{"
                + "fecha=" + fecha
                + ", ingresos=" + ingresos
                + ", gastos=" + gastos
                + ", resultado=" + getResultado()
                + '}';
    }
}
