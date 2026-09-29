package com.restaurante.validator;

import com.restaurante.exception.ParqueaderoLlenoException;
import com.restaurante.exception.RegistroVehiculoNotFoundException;
import com.restaurante.repository.RegistroVehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroVehiculoValidatorTest {

    private RegistroVehiculoRepository registroRepository;
    private RegistroVehiculoValidator  validator;

    @BeforeEach
    void setUp() {
        registroRepository = mock(RegistroVehiculoRepository.class);
        validator          = new RegistroVehiculoValidator(registroRepository);
    }

    @Test
    @DisplayName("validarPlacaNoActiva — placa nueva no lanza")
    void placaNueva_noLanza() {
        when(registroRepository.existsByPlacaIgnoreCaseAndSalidaIsNull("ABC-123"))
                .thenReturn(false);

        assertDoesNotThrow(() -> validator.validarPlacaNoActiva("ABC-123"));
    }

    @Test
    @DisplayName("validarPlacaNoActiva — placa activa lanza excepción")
    void placaActiva_lanza() {
        when(registroRepository.existsByPlacaIgnoreCaseAndSalidaIsNull("ABC-123"))
                .thenReturn(true);

        assertThrows(ParqueaderoLlenoException.class,
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

        assertThrows(ParqueaderoLlenoException.class,
                () -> validator.validarCapacidadDisponible());
    }

    @Test
    @DisplayName("validarPlacaActiva — sin registro lanza excepción")
    void placaNoActiva_lanza() {
        when(registroRepository.findFirstByPlacaIgnoreCaseAndSalidaIsNull("XXX-999"))
                .thenReturn(Optional.empty());

        assertThrows(RegistroVehiculoNotFoundException.class,
                () -> validator.validarPlacaActiva("XXX-999"));
    }
}