package com.restaurante.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CalculoUtilsTest {

    @Test
    @DisplayName("redondear — 2 decimales")
    void redondear_dosDecimales() {
        assertEquals(12.35, CalculoUtils.redondear(12.3456789));
        assertEquals(10.0, CalculoUtils.redondear(10.0));
        assertEquals(0.0, CalculoUtils.redondear(0.0));
    }

    @Test
    @DisplayName("calcularCobroPorMinutos — 60 min a 100/min = 6000")
    void calcularCobro_60min() {
        LocalDateTime entrada = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime salida  = LocalDateTime.of(2026, 1, 1, 11, 0);

        assertEquals(6000.0, CalculoUtils.calcularCobroPorMinutos(entrada, salida));
    }

    @Test
    @DisplayName("calcularCobroPorMinutos — 30 min = 3000")
    void calcularCobro_30min() {
        LocalDateTime entrada = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime salida  = LocalDateTime.of(2026, 1, 1, 10, 30);

        assertEquals(3000.0, CalculoUtils.calcularCobroPorMinutos(entrada, salida));
    }

    @Test
    @DisplayName("calcularCobroPorMinutos — null devuelve 0")
    void calcularCobro_null() {
        assertEquals(0.0, CalculoUtils.calcularCobroPorMinutos(null, LocalDateTime.now()));
        assertEquals(0.0, CalculoUtils.calcularCobroPorMinutos(LocalDateTime.now(), null));
    }

    @Test
    @DisplayName("aplicarDescuento — 10% sobre 10000 = 9000")
    void descuento_10() {
        assertEquals(9000.0, CalculoUtils.aplicarDescuento(10000.0, 10.0));
    }

    @Test
    @DisplayName("aplicarDescuento — 20% sobre 50000 = 40000")
    void descuento_20() {
        assertEquals(40000.0, CalculoUtils.aplicarDescuento(50000.0, 20.0));
    }

    @Test
    @DisplayName("aplicarDescuento — porcentaje inválido devuelve el total")
    void descuento_invalido() {
        assertEquals(10000.0, CalculoUtils.aplicarDescuento(10000.0, -5));
        assertEquals(10000.0, CalculoUtils.aplicarDescuento(10000.0, 150));
    }

    @Test
    @DisplayName("aplicarDescuento — 0% devuelve el total")
    void descuento_cero() {
        assertEquals(10000.0, CalculoUtils.aplicarDescuento(10000.0, 0));
    }
}