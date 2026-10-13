package com.restaurante.util;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Utilidades de cálculo — redondeo, cobros, descuentos.
 */
public final class CalculoUtils {

    private CalculoUtils() {}

    private static final double TARIFA_POR_MINUTO = 100.0;

    /** Redondea a 2 decimales. */
    public static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Calcula el cobro de un parqueadero por minutos. */
    public static double calcularCobroPorMinutos(LocalDateTime entrada, LocalDateTime salida) {
        if (entrada == null || salida == null) return 0.0;
        long minutos = Duration.between(entrada, salida).toMinutes();
        return redondear(minutos * TARIFA_POR_MINUTO);
    }

    /** Aplica un descuento porcentual a un total. */
    public static double aplicarDescuento(double total, double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) return total;
        return redondear(total * (1 - porcentaje / 100.0));
    }
}