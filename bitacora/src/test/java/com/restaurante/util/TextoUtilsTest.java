package com.restaurante.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextoUtilsTest {

    @Test
    @DisplayName("normalizarNombre — capitaliza y limpia espacios")
    void normalizar_capitaliza() {
        assertEquals("Bandeja paisa", TextoUtils.normalizarNombre("  BANDEJA   PAISA  "));
    }

    @Test
    @DisplayName("normalizarNombre — null y vacío devuelven null")
    void normalizar_nullYVacio() {
        assertNull(TextoUtils.normalizarNombre(null));
        assertNull(TextoUtils.normalizarNombre(""));
        assertNull(TextoUtils.normalizarNombre("   "));
    }

    @Test
    @DisplayName("sonIgualesNormalizados — case-insensitive")
    void sonIguales_caseInsensitive() {
        assertTrue(TextoUtils.sonIgualesNormalizados("Salmón", "SALMÓN"));
        assertTrue(TextoUtils.sonIgualesNormalizados(" roll ", "ROLL"));
        assertFalse(TextoUtils.sonIgualesNormalizados("A", "B"));
    }

    @Test
    @DisplayName("sonIgualesNormalizados — null devuelve false")
    void sonIguales_null() {
        assertFalse(TextoUtils.sonIgualesNormalizados(null, "X"));
        assertFalse(TextoUtils.sonIgualesNormalizados("X", null));
        assertFalse(TextoUtils.sonIgualesNormalizados(null, null));
    }

    @Test
    @DisplayName("generarCodigo — formato correcto")
    void generarCodigo_formato() {
        assertEquals("PLT-0001", TextoUtils.generarCodigo("PLT-", 1L));
        assertEquals("RES-0042", TextoUtils.generarCodigo("RES-", 42L));
        assertEquals("PAR-9999", TextoUtils.generarCodigo("PAR-", 9999L));
    }

    @Test
    @DisplayName("generarCodigo — null devuelve null")
    void generarCodigo_null() {
        assertNull(TextoUtils.generarCodigo(null, 1L));
        assertNull(TextoUtils.generarCodigo("X-", null));
    }
}