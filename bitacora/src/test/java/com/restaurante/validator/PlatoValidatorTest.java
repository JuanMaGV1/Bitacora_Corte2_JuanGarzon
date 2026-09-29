package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.repository.PlatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlatoValidatorTest {

    private PlatoRepository platoRepository;
    private PlatoValidator  validator;

    @BeforeEach
    void setUp() {
        platoRepository = mock(PlatoRepository.class);
        validator       = new PlatoValidator(platoRepository);
    }

    // ─── validarNombreUnico ──────────────────────────────────────────

    @Test
    @DisplayName("validarNombreUnico — nombre nuevo no lanza")
    void validarNombreUnico_nombreNuevo_noLanza() {
        when(platoRepository.existsByNombreIgnoreCase("California Roll")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNombreUnico("California Roll"));
    }

    @Test
    @DisplayName("validarNombreUnico — duplicado lanza PlatoAlreadyExistsException")
    void validarNombreUnico_duplicado_lanza() {
        when(platoRepository.existsByNombreIgnoreCase("Ajiaco")).thenReturn(true);

        PlatoAlreadyExistsException ex = assertThrows(
                PlatoAlreadyExistsException.class,
                () -> validator.validarNombreUnico("Ajiaco"));

        assertTrue(ex.getMessage().contains("Ajiaco"));
    }

    // ─── validarNombreUnicoExcluyendo ────────────────────────────────

    @Test
    @DisplayName("validarNombreUnicoExcluyendo — nombre único no lanza")
    void validarNombreUnicoExcluyendo_unico_noLanza() {
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Sashimi", 1L)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNombreUnicoExcluyendo("Sashimi", 1L));
    }

    @Test
    @DisplayName("validarNombreUnicoExcluyendo — duplicado lanza")
    void validarNombreUnicoExcluyendo_duplicado_lanza() {
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Sashimi", 1L)).thenReturn(true);

        assertThrows(PlatoAlreadyExistsException.class,
                () -> validator.validarNombreUnicoExcluyendo("Sashimi", 1L));
    }

    // ─── validarPrecioRazonable ──────────────────────────────────────

    @Test
    @DisplayName("validarPrecioRazonable — precio normal no lanza")
    void validarPrecio_normal_noLanza() {
        assertDoesNotThrow(() -> validator.validarPrecioRazonable(28000.0));
    }

    @Test
    @DisplayName("validarPrecioRazonable — precio excesivo lanza")
    void validarPrecio_excesivo_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarPrecioRazonable(2_000_000.0));
    }

    @Test
    @DisplayName("validarPrecioRazonable — null no lanza")
    void validarPrecio_null_noLanza() {
        assertDoesNotThrow(() -> validator.validarPrecioRazonable(null));
    }

    @Test
    @DisplayName("validarPrecioRazonable — precio en el límite no lanza")
    void validarPrecio_enLimite_noLanza() {
        assertDoesNotThrow(() -> validator.validarPrecioRazonable(1_000_000.0));
    }

    // ─── validarTandaDeRolls ─────────────────────────────────────────

    @Test
    @DisplayName("validarTandaDeRolls — 6 rolls no lanza")
    void validarTanda_6rolls_noLanza() {
        assertDoesNotThrow(() -> validator.validarTandaDeRolls(6));
    }

    @Test
    @DisplayName("validarTandaDeRolls — 7 rolls lanza")
    void validarTanda_7rolls_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarTandaDeRolls(7));
    }
}