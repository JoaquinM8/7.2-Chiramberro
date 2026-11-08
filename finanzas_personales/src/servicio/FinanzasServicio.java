package servicio;

import modelo.ResumenMensual;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio de reglas financieras.
 *
 * <p>Concentra los calculos de negocio y no depende de Swing ni de JDBC. La
 * interfaz del futuro formulario podra llamar a estos metodos sin duplicar
 * formulas. Todos los importes se manejan con {@link BigDecimal} y se
 * redondean con {@link RoundingMode#HALF_UP} a dos decimales.</p>
 */
public class FinanzasServicio {

    private static final BigDecimal CIEN = new BigDecimal("100");

    /* =============================================================
       1) INDICADORES DEL PERIODO
       ============================================================= */

    /**
     * Calcula la tasa de ahorro del periodo:
     * {@code resultado / ingresos * 100}.
     *
     * <p>Si el resultado es negativo, la tasa tambien sera negativa: describe
     * un deficit del periodo, no una recomendacion financiera.</p>
     *
     * @param resumen resumen mensual con ingresos, gastos y resultado.
     * @return tasa de ahorro con dos decimales.
     * @throws IllegalArgumentException si el resumen es nulo o no tiene
     *                                  ingresos positivos.
     */
    public BigDecimal calcularTasaAhorro(ResumenMensual resumen) {
        if (resumen == null) {
            throw new IllegalArgumentException("Resumen obligatorio.");
        }

        BigDecimal ingresos = resumen.getIngresos();
        if (ingresos == null || ingresos.signum() <= 0) {
            throw new IllegalArgumentException(
                    "No se calcula tasa de ahorro sin ingresos positivos.");
        }

        return resumen.getResultado()
                .multiply(CIEN)
                .divide(ingresos, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el porcentaje que representa un gasto de categoria sobre el
     * total de gastos del periodo.
     *
     * @param gastoCategoria gasto de la categoria (no nulo y no negativo).
     * @param gastosTotales  total de gastos (no nulo y mayor que cero).
     * @return porcentaje con dos decimales.
     * @throws IllegalArgumentException si los importes no son validos.
     */
    public BigDecimal calcularPorcentajeGasto(BigDecimal gastoCategoria,
                                              BigDecimal gastosTotales) {
        if (gastoCategoria == null || gastosTotales == null
                || gastoCategoria.signum() < 0
                || gastosTotales.signum() <= 0) {
            throw new IllegalArgumentException("Importes no validos.");
        }

        return gastoCategoria.multiply(CIEN)
                .divide(gastosTotales, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula que porcentaje de los ingresos representa el gasto total.
     *
     * <p>Es una consulta de ampliacion: si el gasto supera al ingreso, el
     * porcentaje sera mayor que 100.</p>
     *
     * @param resumen resumen del periodo.
     * @return gastos / ingresos * 100.
     * @throws IllegalArgumentException si el resumen es nulo o no tiene
     *                                  ingresos positivos.
     */
    public BigDecimal calcularPorcentajeGastoSobreIngresos(ResumenMensual resumen) {
        if (resumen == null) {
            throw new IllegalArgumentException("Resumen obligatorio.");
        }

        BigDecimal ingresos = resumen.getIngresos();
        if (ingresos == null || ingresos.signum() <= 0) {
            throw new IllegalArgumentException(
                    "No se calcula el porcentaje de gasto sin ingresos positivos.");
        }

        return resumen.getGastos()
                .multiply(CIEN)
                .divide(ingresos, 2, RoundingMode.HALF_UP);
    }

    /* =============================================================
       2) INDICADORES DE PRESUPUESTO
       ============================================================= */

    /**
     * Interpreta la diferencia entre el monto planificado y el gasto real.
     *
     * @param disponible monto planificado menos gasto real.
     * @return DENTRO DEL PRESUPUESTO, AL LIMITE o EXCEDIDO.
     */
    public String interpretarDisponible(BigDecimal disponible) {
        if (disponible == null) {
            return "SIN DATOS";
        }
        if (disponible.signum() > 0) {
            return "DENTRO DEL PRESUPUESTO";
        }
        if (disponible.signum() == 0) {
            return "AL LIMITE";
        }
        return "EXCEDIDO";
    }

    /**
     * Calcula el porcentaje ejecutado de un presupuesto.
     *
     * @param gastoReal       gasto real del periodo.
     * @param montoPlanificado monto planificado (mayor que cero).
     * @return gasto real / planificado * 100.
     * @throws IllegalArgumentException si los importes no son validos.
     */
    public BigDecimal calcularPorcentajeEjecucionPresupuesto(BigDecimal gastoReal,
                                                             BigDecimal montoPlanificado) {
        if (gastoReal == null || montoPlanificado == null
                || gastoReal.signum() < 0
                || montoPlanificado.signum() <= 0) {
            throw new IllegalArgumentException("Importes no validos.");
        }

        return gastoReal.multiply(CIEN)
                .divide(montoPlanificado, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el gasto promedio por movimiento.
     *
     * @param totalGastos        suma de gastos del periodo.
     * @param cantidadMovimientos cantidad de movimientos de tipo GASTO.
     * @return promedio con dos decimales; cero si no hubo movimientos.
     * @throws IllegalArgumentException si el total es nulo o negativo.
     */
    public BigDecimal calcularPromedioGasto(BigDecimal totalGastos,
                                            long cantidadMovimientos) {
        if (totalGastos == null || totalGastos.signum() < 0) {
            throw new IllegalArgumentException("El total de gastos no es valido.");
        }
        if (cantidadMovimientos <= 0) {
            return BigDecimal.ZERO;
        }

        return totalGastos.divide(
                BigDecimal.valueOf(cantidadMovimientos), 2, RoundingMode.HALF_UP);
    }
}
