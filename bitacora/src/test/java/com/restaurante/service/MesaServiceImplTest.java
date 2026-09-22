package com.restaurante.service;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.validator.MesaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MesaServiceImplTest {

    private MesaValidator validator;
    private MesaServiceImpl service;

    @BeforeEach
    void setUp() {
        validator = mock(MesaValidator.class);
        service   = new MesaServiceImpl(validator);
    }

    private Mesa mesa(int numero, int cap) {
        return Mesa.builder().numero(numero).capacidad(cap).build();
    }

    @Test
    @DisplayName("crear — mesa válida queda DISPONIBLE")
    void crear_valida_creaDisponible() {
        Mesa creada = service.crear(mesa(1, 4));

        assertNotNull(creada.getId());
        assertEquals(EstadoMesa.DISPONIBLE, creada.getEstado());
        assertFalse(creada.tieneCuentaAbierta());
    }

    @Test
    @DisplayName("abrirCuenta — cambia a OCUPADA")
    void abrirCuenta_cambiaEstado() {
        Mesa creada = service.crear(mesa(1, 4));
        Mesa abierta = service.abrirCuenta(creada.getId());

        assertEquals(EstadoMesa.OCUPADA, abierta.getEstado());
        assertTrue(abierta.tieneCuentaAbierta());
    }

    @Test
    @DisplayName("abrirCuenta — ya abierta lanza excepción")
    void abrirCuenta_yaAbierta_lanzaExcepcion() {
        Mesa creada = service.crear(mesa(1, 4));
        doThrow(new MesaNoDisponibleException("ya abierta"))
                .when(validator).validarAperturaCuenta(any());

        assertThrows(MesaNoDisponibleException.class,
                () -> service.abrirCuenta(creada.getId()));
    }

    @Test
    @DisplayName("cerrarCuenta — vuelve a DISPONIBLE")
    void cerrarCuenta_cambiaEstado() {
        Mesa creada = service.crear(mesa(1, 4));
        service.abrirCuenta(creada.getId());
        Mesa cerrada = service.cerrarCuenta(creada.getId());

        assertEquals(EstadoMesa.DISPONIBLE, cerrada.getEstado());
        assertFalse(cerrada.tieneCuentaAbierta());
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza MesaNotFoundException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(MesaNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("obtenerDisponibles — filtra correctamente")
    void obtenerDisponibles_filtra() {
        Mesa m1 = service.crear(mesa(1, 4));
        Mesa m2 = service.crear(mesa(2, 2));
        service.abrirCuenta(m1.getId());

        assertEquals(1, service.obtenerDisponibles().size());
    }

    @Test
    @DisplayName("obtenerTodas — lista vacía al inicio")
    void obtenerTodas_vacio() {
        assertTrue(service.obtenerTodas().isEmpty());
    }
}