package com.restaurante.service.impl;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.MesaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MesaServiceImpl — pruebas unitarias")
class MesaServiceImplTest {

    @Mock private MesaRepository   mesaRepository;
    @Mock private MesaEntityMapper entityMapper;
    @Mock private MesaValidator    validator;

    @InjectMocks private MesaServiceImpl service;

    private MesaEntity entity;
    private Mesa       domain;

    @BeforeEach
    void setUp() {
        entity = MesaEntity.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
        domain = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
    }

    @Test
    @DisplayName("crear — guarda mesa en DISPONIBLE")
    void crear_ok() {
        when(mesaRepository.save(any(MesaEntity.class))).thenReturn(entity);
        when(entityMapper.toEntity(any())).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        Mesa resultado = service.crear(Mesa.builder().numero(1).capacidad(4).build());

        assertEquals(EstadoMesa.DISPONIBLE, resultado.getEstado());
        assertFalse(resultado.tieneCuentaAbierta());
        verify(validator).validarNumeroUnico(1);
    }

    @Test
    @DisplayName("obtenerTodas — devuelve lista mapeada")
    void obtenerTodas_ok() {
        when(mesaRepository.findAll()).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        assertEquals(1, service.obtenerTodas().size());
    }

    @Test
    @DisplayName("obtenerDisponibles — filtra por DISPONIBLE")
    void obtenerDisponibles_ok() {
        when(mesaRepository.findByEstado(EstadoMesa.DISPONIBLE)).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        assertEquals(1, service.obtenerDisponibles().size());
        verify(mesaRepository).findByEstado(EstadoMesa.DISPONIBLE);
    }

    @Test
    @DisplayName("obtenerPorId — id inexistente lanza excepción")
    void obtenerPorId_noExiste() {
        when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("abrirCuenta — cambia a OCUPADA y cuentaAbierta=true")
    void abrirCuenta_ok() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        MesaEntity abierta = MesaEntity.builder().id(1L).numero(1)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build();
        when(mesaRepository.save(any())).thenReturn(abierta);
        when(entityMapper.toDomain(abierta)).thenReturn(Mesa.builder()
                .id(1L).numero(1).estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build());

        Mesa resultado = service.abrirCuenta(1L);

        assertTrue(resultado.tieneCuentaAbierta());
        assertEquals(EstadoMesa.OCUPADA, resultado.getEstado());
    }

    @Test
    @DisplayName("cerrarCuenta — si no tiene cuenta abierta lanza excepción")
    void cerrarCuenta_sinCuenta_lanza() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        doThrow(new EstadoInvalidoException("no abierta"))
                .when(validator).validarCierreCuenta(any());

        assertThrows(EstadoInvalidoException.class, () -> service.cerrarCuenta(1L));
    }

    @Test
    @DisplayName("eliminar — con cuenta abierta lanza excepción")
    void eliminar_conCuenta_lanza() {
        Mesa ocupada = Mesa.builder().id(1L).numero(1).cuentaAbierta(true).build();
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(ocupada);

        assertThrows(EstadoInvalidoException.class, () -> service.eliminar(1L));
        verify(mesaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar — sin cuenta abierta borra")
    void eliminar_ok() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        service.eliminar(1L);

        verify(mesaRepository).deleteById(1L);
    }
}