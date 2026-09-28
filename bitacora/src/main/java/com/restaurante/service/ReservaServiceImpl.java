package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.validator.ReservaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final Map<Long, Reserva> reservas = new ConcurrentHashMap<>();
    private final AtomicLong         contador = new AtomicLong(1);

    private final ReservaValidator validator;
    private final MesaService mesaService;

    @Override
    public List<Reserva> obtenerTodas() {
        return reservas.values().stream().toList();
    }

    @Override
    public List<Reserva> obtenerPorCliente(String cliente) {
        return reservas.values().stream()
                .filter(r -> r.getCliente().equalsIgnoreCase(cliente))
                .toList();
    }

    @Override
    public List<Reserva> obtenerVigentes() {
        return reservas.values().stream()
                .filter(Reserva::estaVigente)
                .toList();
    }

    @Override
    public Reserva obtenerPorId(Long id) {
        return reservas.values().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ReservaNotFoundException("Reserva", id));
    }

    @Override
    public Reserva crear(Reserva reserva) {
        // ─── Validación 1: la mesa debe existir ─────────────────────
        Mesa mesa = mesaService.obtenerPorId(reserva.getIdMesa());

        // ─── Validación 2: comensales ≤ capacidad de la mesa ────────
        validator.validarComensalesContraCapacidad(reserva, mesa.getCapacidad());

        // ─── Validación 3: sin conflicto de horario ─────────────────
        validator.validarSinConflictoHorario(reserva, reservas.values());

        reserva.setId(contador.getAndIncrement());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reservas.put(reserva.getId(), reserva);

        log.info("Reserva creada: id={}, cliente={}, mesa={}, comensales={}",
                reserva.getId(), reserva.getCliente(),
                reserva.getIdMesa(), reserva.getComensales());
        return reserva;
    }

    @Override
    public Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = obtenerPorId(id);
        validator.validarTransicion(reserva, nuevoEstado);
        reserva.setEstado(nuevoEstado);
        log.info("Reserva #{} → {}", id, nuevoEstado);
        return reserva;
    }

    @Override
    public void cancelar(Long id) {
        Reserva reserva = obtenerPorId(id);
        validator.validarCancelable(reserva);
        reserva.setEstado(EstadoReserva.CANCELADA);
        log.info("Reserva #{} cancelada", id);
    }
}