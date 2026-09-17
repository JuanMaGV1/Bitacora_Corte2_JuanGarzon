package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.model.domain.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de PlatoValidator.
 * SIN mocks — el Validator no tiene dependencias, se prueba la lógica real.
 */
class PlatoValidatorTest {

    private PlatoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PlatoValidator();
    }

    // ─── validarNombreUnico ─────────────────────────────────────────────

    @Test
    @DisplayName("✅ validarNombreUnico — nombre nuevo no lanza excepción")
    void validarNombreUnico_nombreNuevo_noLanza() {
        List<Plato> existentes = List.of(
                Plato.builder().nombre("Ajiaco").build()
        );

        assertDoesNotThrow(() ->
                validator.validarNombreUnico("Bandeja Paisa", existentes));
    }

    @Test
    @DisplayName("❌ validarNombreUnico — nombre duplicado lanza PlatoAlreadyExistsException")
    void validarNombreUnico_nombreDuplicado_lanzaExcepcion() {
        List<Plato> existentes = List.of(
                Plato.builder().nombre("Ajiaco").build()
        );

        PlatoAlreadyExistsException ex = assertThrows(
                PlatoAlreadyExistsException.class,
                () -> validator.validarNombreUnico("Ajiaco", existentes)
        );

        assertTrue(ex.getMessage().contains("Ajiaco"));
    }

    @Test
    @DisplayName("❌ validarNombreUnico — comparación case-insensitive")
    void validarNombreUnico_caseInsensitive_lanzaExcepcion() {
        List<Plato> existentes = List.of(
                Plato.builder().nombre("Ajiaco").build()
        );

        assertThrows(
                PlatoAlreadyExistsException.class,
                () -> validator.validarNombreUnico("AJIACO", existentes)
        );
        assertThrows(
                PlatoAlreadyExistsException.class,
                () -> validator.validarNombreUnico("ajiaco", existentes)
        );
    }

    @Test
    @DisplayName("✅ validarNombreUnico — lista vacía no lanza excepción")
    void validarNombreUnico_listaVacia_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarNombreUnico("Cualquiera", List.of()));
    }

    // ─── validarPrecioRazonable ─────────────────────────────────────────

    @Test
    @DisplayName("✅ validarPrecioRazonable — precio normal no lanza excepción")
    void validarPrecioRazonable_precioNormal_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarPrecioRazonable(28000.0));
    }

    @Test
    @DisplayName("❌ validarPrecioRazonable — precio mayor al máximo lanza excepción")
    void validarPrecioRazonable_precioExcesivo_lanzaExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validarPrecioRazonable(2_000_000.0)
        );
    }

    @Test
    @DisplayName("✅ validarPrecioRazonable — precio null no lanza (se valida en DTO)")
    void validarPrecioRazonable_precioNull_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarPrecioRazonable(null));
    }

    @Test
    @DisplayName("✅ validarPrecioRazonable — precio justo en el límite no lanza")
    void validarPrecioRazonable_precioEnElLimite_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarPrecioRazonable(1_000_000.0));
    }
}