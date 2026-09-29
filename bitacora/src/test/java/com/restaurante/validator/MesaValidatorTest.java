package com.restaurante.validator;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.repository.MesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MesaValidatorTest {

    private MesaRepository mesaRepository;
    private MesaValidator  validator;

    @BeforeEach
    void setUp() {
        mesaRepository = mock(MesaRepository.class);
        validator      = new MesaValidator(mesaRepository);
    }

    @Test
    @DisplayName("validarNumeroUnico — número nuevo no lanza")
    void validarNumeroUnico_nuevo_noLanza() {
        when(mesaRepository.existsByNumero(5)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNumeroUnico(5));
    }

    @Test
    @DisplayName("validarNumeroUnico — duplicado lanza excepción")
    void validarNumeroUnico_duplicado_lanza() {
        when(mesaRepository.existsByNumero(1)).thenReturn(true);

        assertThrows(MesaNoDisponibleException.class,
                () -> validator.validarNumeroUnico(1));
    }

    @Test
    @DisplayName("validarAperturaCuenta — ya abierta lanza excepción")
    void validarAperturaCuenta_yaAbierta_lanza() {
        Mesa m = Mesa.builder().numero(1).cuentaAbierta(true)
                .estado(EstadoMesa.OCUPADA).build();

        assertThrows(MesaNoDisponibleException.class,
                () -> validator.validarAperturaCuenta(m));
    }

    @Test
    @DisplayName("validarAperturaCuenta — cerrada no lanza")
    void validarAperturaCuenta_cerrada_noLanza() {
        Mesa m = Mesa.builder().numero(1).cuentaAbierta(false)
                .estado(EstadoMesa.DISPONIBLE).build();

        assertDoesNotThrow(() -> validator.validarAperturaCuenta(m));
    }

    @Test
    @DisplayName("validarCierreCuenta — sin cuenta lanza excepción")
    void validarCierreCuenta_sinCuenta_lanza() {
        Mesa m = Mesa.builder().numero(1).cuentaAbierta(false).build();

        assertThrows(MesaNoDisponibleException.class,
                () -> validator.validarCierreCuenta(m));
    }

    @Test
    @DisplayName("validarCierreCuenta — con cuenta no lanza")
    void validarCierreCuenta_conCuenta_noLanza() {
        Mesa m = Mesa.builder().numero(1).cuentaAbierta(true)
                .estado(EstadoMesa.OCUPADA).build();

        assertDoesNotThrow(() -> validator.validarCierreCuenta(m));
    }
}