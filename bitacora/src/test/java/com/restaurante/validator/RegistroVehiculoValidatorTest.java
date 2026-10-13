package com.restaurante.validator;

import com.restaurante.exception.ConflictoException;
import com.restaurante.repository.RegistroVehiculoRepository;
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
@DisplayName("RegistroVehiculoValidator — pruebas unitarias")
class RegistroVehiculoValidatorTest {

    @Mock private RegistroVehiculoRepository registroRepository;
    @InjectMocks private RegistroVehiculoValidator validator;

    @Test
    @DisplayName("validarPlacaNoActiva — placa nueva no lanza")
    void placaNueva_noLanza() {
        when(registroRepository.existsByPlacaIgnoreCaseAndSalidaIsNull("ABC-123"))
                .thenReturn(false);

        assertDoesNotThrow(() -> validator.validarPlacaNoActiva("ABC-123"));
    }

    @Test
    @DisplayName("validarPlacaNoActiva — placa activa lanza ConflictoException")
    void placaActiva_lanza() {
        when(registroRepository.existsByPlacaIgnoreCaseAndSalidaIsNull("ABC-123"))
                .thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> validator.validarPlacaNoActiva("ABC-123"));
    }

    @Test
    @DisplayName("validarCapacidadDisponible — con cupo no lanza")
    void capacidad_conCupo() {
        when(registroRepository.countActivos()).thenReturn(19L);

        assertDoesNotThrow(() -> validator.validarCapacidadDisponible());
    }

    @Test
    @DisplayName("validarCapacidadDisponible — parqueadero lleno lanza")
    void capacidad_lleno_lanza() {
        when(registroRepository.countActivos()).thenReturn(20L);

        assertThrows(ConflictoException.class, () -> validator.validarCapacidadDisponible());
    }
}