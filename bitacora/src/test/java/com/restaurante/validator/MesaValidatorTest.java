package com.restaurante.validator;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MesaValidatorTest {

    private final MesaValidator validator = new MesaValidator();

    @Test
    @DisplayName("validarNumeroUnico — número nuevo no lanza")
    void numeroNuevo_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarNumeroUnico(5, List.of(
                        Mesa.builder().numero(1).build())));
    }

    @Test
    @DisplayName("validarNumeroUnico — duplicado lanza excepción")
    void numeroDuplicado_lanza() {
        assertThrows(MesaNoDisponibleException.class, () ->
                validator.validarNumeroUnico(1, List.of(
                        Mesa.builder().numero(1).build())));
    }

    @Test
    @DisplayName("validarAperturaCuenta — ya abierta lanza excepción")
    void aperturaCuenta_yaAbierta() {
        Mesa m = Mesa.builder().numero(1).cuentaAbierta(true)
                .estado(EstadoMesa.OCUPADA).build();

        assertThrows(MesaNoDisponibleException.class,
                () -> validator.validarAperturaCuenta(m));
    }

    @Test
    @DisplayName("validarCierreCuenta — sin cuenta lanza excepción")
    void cierreCuenta_sinCuenta() {
        Mesa m = Mesa.builder().numero(1).cuentaAbierta(false).build();

        assertThrows(MesaNoDisponibleException.class,
                () -> validator.validarCierreCuenta(m));
    }
}