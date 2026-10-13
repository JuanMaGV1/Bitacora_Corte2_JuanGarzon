package com.restaurante.service.impl;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.ReservaEntityMapper;
import com.restaurante.model.domain.*;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.ReservaRepository;
import com.restaurante.service.MesaService;
import com.restaurante.validator.ReservaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaServiceImpl — pruebas unitarias")
class ReservaServiceImplTest {

    @Mock private ReservaRepository   reservaRepository;
    @Mock private ReservaEntityMapper entityMapper;
    @Mock private ReservaValidator    validator;
    @Mock private MesaService         mesaService;

    @InjectMocks private ReservaServiceImpl service;

    @Test
    @DisplayName("crear — reserva válida queda PENDIENTE")
    void crear_ok() {
        Reserva nueva = Reserva.builder()
                .idMesa(1L).cliente("Juan")
                .fechaHora(LocalDateTime.now().plusDays(1))
                .comensales(2).build();

        Mesa mesa = Mesa.builder().id(1L).numero(1).capacidad(4).build();
        ReservaEntity guardada = ReservaEntity.builder()
                .id(1L).idMesa(1L).cliente("Juan")
                .fechaHora(nueva.getFechaHora()).comensales(2)
                .estado(EstadoReserva.PENDIENTE).build();
        Reserva dominio = Reserva.builder()
                .id(1L).idMesa(1L).cliente("Juan").estado(EstadoReserva.PENDIENTE).build();

        when(mesaService.obtenerPorId(1L)).thenReturn(mesa);
        when(reservaRepository.save(any())).thenReturn(guardada);
        when(entityMapper.toDomain(guardada)).thenReturn(dominio);

        Reserva resultado = service.crear(nueva);

        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
        verify(validator).validarComensalesContraCapacidad(nueva, 4);
        verify(validator).validarSinConflictoHorario(nueva);
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza excepción")
    void obtenerPorId_noExiste() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("obtenerVigentes — devuelve solo PENDIENTE y CONFIRMADA")
    void obtenerVigentes_ok() {
        ReservaEntity entity = ReservaEntity.builder()
                .id(1L).estado(EstadoReserva.PENDIENTE).build();
        Reserva dominio = Reserva.builder().id(1L).estado(EstadoReserva.PENDIENTE).build();

        when(reservaRepository.findByEstadoIn(any())).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);

        assertEquals(1, service.obtenerVigentes().size());
    }

    @Test
    @DisplayName("cambiarEstado — transición válida")
    void cambiarEstado_ok() {
        ReservaEntity entity = ReservaEntity.builder()
                .id(1L).estado(EstadoReserva.PENDIENTE).build();
        ReservaEntity confirmada = ReservaEntity.builder()
                .id(1L).estado(EstadoReserva.CONFIRMADA).build();
        Reserva dominio = Reserva.builder().id(1L).estado(EstadoReserva.CONFIRMADA).build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(
                Reserva.builder().id(1L).estado(EstadoReserva.PENDIENTE).build());
        when(reservaRepository.save(entity)).thenReturn(confirmada);
        when(entityMapper.toDomain(confirmada)).thenReturn(dominio);

        Reserva resultado = service.cambiarEstado(1L, EstadoReserva.CONFIRMADA);

        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());
    }

    @Test
    @DisplayName("cancelar — completada lanza excepción")
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
}