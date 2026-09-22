package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReservaConflictoException;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaValidatorTest {

    private final ReservaValidator validator = new ReservaValidator();

    private Reserva reserva(Long mesa, LocalDateTime fecha, EstadoReserva estado) {
        return Reserva.builder().idMesa(mesa).fechaHora(fecha)
                .estado(estado).cliente("X").comensales(2).build();
    }

    @Test
    @DisplayName("sin conflicto — mesas distintas")
    void sinConflicto_mesasDistintas() {
        Reserva nueva = reserva(2L, LocalDateTime.now().plusDays(1), EstadoReserva.PENDIENTE);
        List<Reserva> existentes = List.of(
                reserva(1L, LocalDateTime.now().plusDays(1), EstadoReserva.PENDIENTE));

        assertDoesNotThrow(() -> validator.validarSinConflictoHorario(nueva, existentes));
    }

    @Test
    @DisplayName("conflicto — misma mesa, horario solapado")
    void conflicto_mismaMesa() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        Reserva nueva = reserva(1L, base, EstadoReserva.PENDIENTE);
        List<Reserva> existentes = List.of(
                reserva(1L, base.plusMinutes(30), EstadoReserva.PENDIENTE));

        assertThrows(ReservaConflictoException.class,
                () -> validator.validarSinConflictoHorario(nueva, existentes));
    }

    @Test
    @DisplayName("sin conflicto — reserva existente cancelada se ignora")
    void sinConflicto_cancelada() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        Reserva nueva = reserva(1L, base, EstadoReserva.PENDIENTE);
        List<Reserva> existentes = List.of(
                reserva(1L, base.plusMinutes(30), EstadoReserva.CANCELADA));

        assertDoesNotThrow(() -> validator.validarSinConflictoHorario(nueva, existentes));
    }

    @Test
    @DisplayName("validarTransicion — inválida lanza excepción")
    void transicion_invalida() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.COMPLETADA);

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicion(r, EstadoReserva.PENDIENTE));
    }

    @Test
    @DisplayName("validarCancelable — completada no se cancela")
    void cancelable_completada() {
        Reserva r = reserva(1L, LocalDateTime.now(), EstadoReserva.COMPLETADA);

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarCancelable(r));
    }
}