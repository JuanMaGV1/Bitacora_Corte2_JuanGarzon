package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaEntityMapper;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.ReservaRepository;
import com.restaurante.validator.ReservaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservaServiceImplTest {

    private ReservaRepository   reservaRepository;
    private ReservaEntityMapper entityMapper;
    private ReservaValidator    validator;
    private MesaService         mesaService;
    private ReservaServiceImpl  service;

    @BeforeEach
    void setUp() {
        reservaRepository = mock(ReservaRepository.class);
        entityMapper      = mock(ReservaEntityMapper.class);
        validator         = mock(ReservaValidator.class);
        mesaService       = mock(MesaService.class);
        service           = new ReservaServiceImpl(
                reservaRepository, entityMapper, validator, mesaService);
    }

    private Reserva reserva() {
        return Reserva.builder()
                .idMesa(3L).cliente("Juan")
                .fechaHora(LocalDateTime.now().plusDays(1))
                .comensales(4).build();
    }

    private Mesa mesaCapacidad6() {
        return Mesa.builder().id(3L).numero(3).capacidad(6).build();
    }

    @Test
    @DisplayName("✅ crear — reserva válida queda PENDIENTE")
    void crear_valida_ok() {
        when(mesaService.obtenerPorId(3L)).thenReturn(mesaCapacidad6());

        ReservaEntity guardada = ReservaEntity.builder()
                .id(1L).idMesa(3L).cliente("Juan")
                .fechaHora(reserva().getFechaHora())
                .comensales(4).estado(EstadoReserva.PENDIENTE).build();
        Reserva dominio = Reserva.builder().id(1L).idMesa(3L)
                .cliente("Juan").estado(EstadoReserva.PENDIENTE).build();

        when(reservaRepository.save(any())).thenReturn(guardada);
        when(entityMapper.toDomain(guardada)).thenReturn(dominio);

        Reserva resultado = service.crear(reserva());

        assertNotNull(resultado.getId());
        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
    }

    @Test
    @DisplayName("❌ obtenerPorId — no existe lanza")
    void obtenerPorId_noExiste() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("✅ cambiarEstado — transición válida")
    void cambiarEstado_valido() {
        ReservaEntity entity = ReservaEntity.builder()
                .id(1L).idMesa(3L).estado(EstadoReserva.PENDIENTE).build();
        ReservaEntity actualizada = ReservaEntity.builder()
                .id(1L).idMesa(3L).estado(EstadoReserva.CONFIRMADA).build();
        Reserva dominio = Reserva.builder().id(1L).estado(EstadoReserva.CONFIRMADA).build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(
                Reserva.builder().id(1L).estado(EstadoReserva.PENDIENTE).build());
        when(reservaRepository.save(entity)).thenReturn(actualizada);
        when(entityMapper.toDomain(actualizada)).thenReturn(dominio);

        Reserva resultado = service.cambiarEstado(1L, EstadoReserva.CONFIRMADA);

        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());
    }

    @Test
    @DisplayName("❌ cancelar — completada lanza")
    void cancelar_completada_lanza() {
        ReservaEntity entity = ReservaEntity.builder()
                .id(1L).estado(EstadoReserva.COMPLETADA).build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(
                Reserva.builder().id(1L).estado(EstadoReserva.COMPLETADA).build());
        doThrow(new EstadoInvalidoException("no cancelable"))
                .when(validator).validarCancelable(any());

        assertThrows(EstadoInvalidoException.class, () -> service.cancelar(1L));
    }

    @Test
    @DisplayName("✅ obtenerTodas — lista vacía")
    void obtenerTodas_vacio() {
        when(reservaRepository.findAll()).thenReturn(List.of());

        assertTrue(service.obtenerTodas().isEmpty());
    }
}