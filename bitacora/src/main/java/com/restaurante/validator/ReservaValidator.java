package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReservaConflictoException;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaValidator {

    private final ReservaRepository reservaRepository;

    public void validarSinConflictoHorario(Reserva nueva) {
        LocalDateTime desde = nueva.getFechaHora().minusHours(2);
        LocalDateTime hasta = nueva.getFechaHora().plusHours(2);

        List<ReservaEntity> conflictos = reservaRepository
                .findByIdMesaAndEstadoInAndFechaHoraBetween(
                        nueva.getIdMesa(),
                        List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                        desde, hasta);

        if (!conflictos.isEmpty()) {
            throw new ReservaConflictoException(
                    "Ya existe una reserva vigente para la mesa " + nueva.getIdMesa()
                    + " cerca de ese horario");
        }
    }

    public void validarComensalesContraCapacidad(Reserva reserva, int capacidadMesa) {
        if (reserva.getComensales() > capacidadMesa) {
            throw new IllegalArgumentException(
                    "La reserva tiene " + reserva.getComensales()
                    + " comensales, pero la mesa " + reserva.getIdMesa()
                    + " solo tiene capacidad para " + capacidadMesa);
        }
    }

    public void validarTransicion(Reserva reserva, EstadoReserva nuevoEstado) {
        if (!reserva.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new EstadoInvalidoException(
                    "No se puede pasar de " + reserva.getEstado() + " a " + nuevoEstado);
        }
    }

    public void validarCancelable(Reserva reserva) {
        if (!reserva.getEstado().esCancelable()) {
            throw new EstadoInvalidoException(
                    "Una reserva en estado " + reserva.getEstado() + " no puede cancelarse");
        }
    }
}