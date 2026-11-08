package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Resultado agregado de gastos por categoria.
 *
 * <p>Incluye el total gastado, la cantidad de movimientos que contribuyeron y,
 * opcionalmente, el porcentaje que representa sobre el total de gastos del
 * periodo. El porcentaje se calcula en MySQL con {@code ROUND} y
 * {@code NULLIF}; por eso puede venir nulo si la consulta no lo solicita.</p>
 */
public class GastoCategoria {

    /* =============================================================
       ATRIBUTOS
       ============================================================= */
    private final int idCategoria;
    private final String nombreCategoria;
    private final BigDecimal totalGastado;
    private final long cantidadMovimientos;
    private final BigDecimal porcentaje;

    /* =============================================================
       CONSTRUCTORES
       ============================================================= */

    /** Constructor para la consulta de gastos por categoria (sin porcentaje). */
    public GastoCategoria(int idCategoria, String nombreCategoria,
                          BigDecimal totalGastado, long cantidadMovimientos) {
        this(idCategoria, nombreCategoria, totalGastado, cantidadMovimientos, null);
    }

    /** Constructor completo, usado por la consulta con porcentaje. */
    public GastoCategoria(int idCategoria, String nombreCategoria,
                          BigDecimal totalGastado, long cantidadMovimientos,
                          BigDecimal porcentaje) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.totalGastado = totalGastado == null ? BigDecimal.ZERO : totalGastado;
        this.cantidadMovimientos = cantidadMovimientos;
        this.porcentaje = porcentaje;
    }

    /* =============================================================
       GETTERS Y CALCULOS
       ============================================================= */
    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public BigDecimal getTotalGastado() {
        return totalGastado;
    }

    public long getCantidadMovimientos() {
        return cantidadMovimientos;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    /** Gasto promedio por movimiento de la categoria. */
    public BigDecimal getPromedioPorMovimiento() {
        if (cantidadMovimientos <= 0) {
            return BigDecimal.ZERO;
        }
        return totalGastado.divide(
                BigDecimal.valueOf(cantidadMovimientos), 2, RoundingMode.HALF_UP);
    }

    /* =============================================================
       REPRESENTACION DE TEXTO
       ============================================================= */
    @Override
    public String toString() {
        return "GastoCategoria{"
                + "idCategoria=" + idCategoria
                + ", nombreCategoria='" + nombreCategoria + '\''
                + ", totalGastado=" + totalGastado
                + ", cantidadMovimientos=" + cantidadMovimientos
                + ", porcentaje=" + porcentaje
                + '}';
    }
}
