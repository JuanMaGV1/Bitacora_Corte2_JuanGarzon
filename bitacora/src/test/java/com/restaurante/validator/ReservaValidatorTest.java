package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReservaConflictoException;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservaValidatorTest {

    private ReservaRepository reservaRepository;
    private ReservaValidator  validator;

    @BeforeEach
    void setUp() {
        reservaRepository = mock(ReservaRepository.class);
        validator         = new ReservaValidator(reservaRepository);
    }

    private Reserva reserva(Long mesa, LocalDateTime fecha, EstadoReserva estado) {
        return Reserva.builder().idMesa(mesa).fechaHora(fecha)
                .estado(estado).cliente("X").comensales(2).build();
    }

    // ─── validarSinConflictoHorario ──────────────────────────────────

    @Test
    @DisplayName("✅ sin conflicto — no hay reservas cercanas")
    void sinConflicto_noLanza() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        Reserva nueva = reserva(1L, fecha, EstadoReserva.PENDIENTE);

        when(reservaRepository.findByIdMesaAndEstadoInAndFechaHoraBetween(
                eq(1L), anyList(), any(), any()))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> validator.validarSinConflictoHorario(nueva));
    }

    @Test
    @DisplayName("❌ conflicto — ya hay reserva cercana")
    void conflicto_lanza() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        Reserva nueva = reserva(1L, fecha, EstadoReserva.PENDIENTE);

        ReservaEntity existente = ReservaEntity.builder()
                .id(1L).idMesa(1L).fechaHora(fecha.plusMinutes(30))
                .estado(EstadoReserva.PENDIENTE).build();

        when(reservaRepository.findByIdMesaAndEstadoInAndFechaHoraBetween(
                eq(1L), anyList(), any(), any()))
                .thenReturn(List.of(existente));

        assertThrows(ReservaConflictoException.class,
                () -> validator.validarSinConflictoHorario(nueva));
    }

    // ─── validarComensalesContraCapacidad ────────────────────────────

    @Test
    @DisplayName("✅ comensales ≤ capacidad no lanza")
    void comensales_dentroCapacidad_noLanza() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.PENDIENTE);
        r.setComensales(4);

        assertDoesNotThrow(() -> validator.validarComensalesContraCapacidad(r, 6));
    }

    @Test
    @DisplayName("❌ comensales > capacidad lanza")
    void comensales_exceden_lanza() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.PENDIENTE);
        r.setComensales(10);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validarComensalesContraCapacidad(r, 4));
    }

    // ─── validarTransicion ───────────────────────────────────────────

    @Test
    @DisplayName("❌ transición inválida lanza")
    void transicion_invalida_lanza() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.COMPLETADA);

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicion(r, EstadoReserva.PENDIENTE));
    }

    @Test
    @DisplayName("✅ transición válida no lanza")
    void transicion_valida_noLanza() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.PENDIENTE);

        assertDoesNotThrow(() ->
                validator.validarTransicion(r, EstadoReserva.CONFIRMADA));
    }

    // ─── validarCancelable ───────────────────────────────────────────

    @Test
    @DisplayName("❌ cancelar completada lanza")
    void cancelar_completada_lanza() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.COMPLETADA);

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarCancelable(r));
    }

    @Test
    @DisplayName("✅ cancelar pendiente no lanza")
    void cancelar_pendiente_noLanza() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.PENDIENTE);

        assertDoesNotThrow(() -> validator.validarCancelable(r));
    }
}