package com.restaurante.service;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.MesaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MesaServiceImplTest {

    private MesaRepository   mesaRepository;
    private MesaEntityMapper entityMapper;
    private MesaValidator    validator;
    private MesaServiceImpl  service;

    @BeforeEach
    void setUp() {
        mesaRepository = mock(MesaRepository.class);
        entityMapper   = mock(MesaEntityMapper.class);
        validator      = mock(MesaValidator.class);
        service        = new MesaServiceImpl(mesaRepository, entityMapper, validator);
    }

    private Mesa mesa(int numero) {
        return Mesa.builder().numero(numero).capacidad(4).build();
    }

    private MesaEntity entity(Long id, int numero, boolean cuentaAbierta) {
        return MesaEntity.builder()
                .id(id).numero(numero).capacidad(4)
                .estado(cuentaAbierta ? EstadoMesa.OCUPADA : EstadoMesa.DISPONIBLE)
                .cuentaAbierta(cuentaAbierta).build();
    }

    @Test
    @DisplayName("crear — mesa queda DISPONIBLE")
    void crear_ok() {
        MesaEntity guardada = entity(1L, 1, false);
        Mesa dominio = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();

        when(entityMapper.toEntity(any())).thenReturn(MesaEntity.builder().numero(1).build());
        when(mesaRepository.save(any())).thenReturn(guardada);
        when(entityMapper.toDomain(guardada)).thenReturn(dominio);

        Mesa resultado = service.crear(mesa(1));

        assertNotNull(resultado.getId());
        assertEquals(EstadoMesa.DISPONIBLE, resultado.getEstado());
    }

    @Test
    @DisplayName("abrirCuenta — ya abierta lanza excepción")
    void abrirCuenta_yaAbierta_lanza() {
        MesaEntity entity = entity(1L, 1, true);
        Mesa dominio = Mesa.builder().id(1L).numero(1).cuentaAbierta(true).build();

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);
        doThrow(new MesaNoDisponibleException("ya abierta"))
                .when(validator).validarAperturaCuenta(any());

        assertThrows(MesaNoDisponibleException.class, () -> service.abrirCuenta(1L));
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza excepción")
    void obtenerPorId_noExiste() {
        when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("abrirCuenta — cambia a OCUPADA")
    void abrirCuenta_ok() {
        MesaEntity entity = entity(1L, 1, false);
        MesaEntity actualizada = entity(1L, 1, true);
        Mesa dominioAbierto = Mesa.builder().id(1L).numero(1).cuentaAbierta(true).build();

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(
                Mesa.builder().id(1L).numero(1).cuentaAbierta(false).estado(EstadoMesa.DISPONIBLE).build());
        when(mesaRepository.save(any())).thenReturn(actualizada);
        when(entityMapper.toDomain(actualizada)).thenReturn(dominioAbierto);

        Mesa resultado = service.abrirCuenta(1L);

        assertTrue(resultado.tieneCuentaAbierta());
    }

    @Test
    @DisplayName("eliminar — con cuenta abierta lanza excepción")
    void eliminar_conCuenta_lanza() {
        MesaEntity entity = entity(1L, 1, true);
        Mesa dominio = Mesa.builder().id(1L).numero(1).cuentaAbierta(true).build();

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);

        assertThrows(MesaNoDisponibleException.class, () -> service.eliminar(1L));
    }
}