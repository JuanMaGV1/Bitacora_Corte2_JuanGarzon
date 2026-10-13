package com.restaurante.validator;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.repository.PlatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlatoValidator — pruebas unitarias")
class PlatoValidatorTest {

    @Mock private PlatoRepository platoRepository;
    @InjectMocks private PlatoValidator validator;

    // ── validarNombreUnico ─────────────────────────────────────────

    @Test
    @DisplayName("validarNombreUnico — nombre nuevo no lanza")
    void validarNombreUnico_nombreNuevo_noLanza() {
        when(platoRepository.existsByNombreIgnoreCase("California Roll")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNombreUnico("California Roll"));
    }

    @Test
    @DisplayName("validarNombreUnico — duplicado lanza ConflictoException")
    void validarNombreUnico_duplicado_lanza() {
        when(platoRepository.existsByNombreIgnoreCase("Tacos")).thenReturn(true);

        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> validator.validarNombreUnico("Tacos"));

        assertTrue(ex.getMessage().contains("Tacos"));
    }

    // ── validarNombreUnicoExcluyendo ───────────────────────────────

    @Test
    @DisplayName("validarNombreUnicoExcluyendo — único no lanza")
    void validarNombreUnicoExcluyendo_ok() {
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Sashimi", 1L)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNombreUnicoExcluyendo("Sashimi", 1L));
    }

    @Test
    @DisplayName("validarNombreUnicoExcluyendo — duplicado lanza")
    void validarNombreUnicoExcluyendo_duplicado_lanza() {
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Sashimi", 1L)).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> validator.validarNombreUnicoExcluyendo("Sashimi", 1L));
    }

    // ── validarPrecioRazonable ─────────────────────────────────────

    @Test
    @DisplayName("validarPrecioRazonable — precio normal no lanza")
    void validarPrecio_normal_noLanza() {
        assertDoesNotThrow(() -> validator.validarPrecioRazonable(28000.0));
    }

    @Test
    @DisplayName("validarPrecioRazonable — precio excesivo lanza")
    void validarPrecio_excesivo_lanza() {
        assertThrows(ReglaDeNegocioException.class,
                () -> validator.validarPrecioRazonable(2_000_000.0));
    }

    @Test
    @DisplayName("validarPrecioRazonable — null no lanza")
    void validarPrecio_null_noLanza() {
        assertDoesNotThrow(() -> validator.validarPrecioRazonable(null));
    }

    @Test
    @DisplayName("validarPrecioRazonable — precio límite no lanza")
    void validarPrecio_enLimite_noLanza() {
        assertDoesNotThrow(() -> validator.validarPrecioRazonable(1_000_000.0));
    }
}