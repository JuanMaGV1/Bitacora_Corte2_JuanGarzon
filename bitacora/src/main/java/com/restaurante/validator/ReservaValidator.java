package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReservaConflictoException;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class ReservaValidator {

    /** Regla: no pueden solaparse 2 reservas vigentes en la misma mesa. */
    public void validarSinConflictoHorario(Reserva nueva, Collection<Reserva> existentes) {
        boolean conflicto = existentes.stream()
                .filter(Reserva::estaVigente)
                .filter(r -> r.getIdMesa().equals(nueva.getIdMesa()))
                .anyMatch(r -> r.chocaCon(nueva));

        if (conflicto) {
            throw new ReservaConflictoException(
                    "Ya existe una reserva vigente para la mesa " + nueva.getIdMesa()
                    + " cerca de ese horario");
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

    /**
     * Regla: los comensales de una reserva no pueden superar
     * la capacidad máxima de la mesa.
     */
    public void validarComensalesContraCapacidad(Reserva reserva, int capacidadMesa) {
        if (reserva.getComensales() > capacidadMesa) {
            log.warn("Reserva rechazada: {} comensales > capacidad {} de la mesa {}",
                    reserva.getComensales(), capacidadMesa, reserva.getIdMesa());
            throw new IllegalArgumentException(
                    "La reserva tiene " + reserva.getComensales()
                    + " comensales, pero la mesa " + reserva.getIdMesa()
                    + " solo tiene capacidad para " + capacidadMesa);
        }
    }
}