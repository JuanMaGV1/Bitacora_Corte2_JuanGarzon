package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaEntityMapper;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.ReservaRepository;
import com.restaurante.validator.ReservaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository   reservaRepository;
    private final ReservaEntityMapper entityMapper;
    private final ReservaValidator    validator;
    private final MesaService         mesaService;

    @Override
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> obtenerPorCliente(String cliente) {
        return reservaRepository.findByClienteIgnoreCase(cliente).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> obtenerVigentes() {
        return reservaRepository.findByEstadoIn(
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)
        ).stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva", id));
    }

    @Override
    @Transactional
    public Reserva crear(Reserva reserva) {
        // Validar que la mesa exista
        Mesa mesa = mesaService.obtenerPorId(reserva.getIdMesa());

        // Validar comensales ≤ capacidad
        validator.validarComensalesContraCapacidad(reserva, mesa.getCapacidad());

        // Validar conflicto de horario
        validator.validarSinConflictoHorario(reserva);

        reserva.setEstado(EstadoReserva.PENDIENTE);
        ReservaEntity guardada = reservaRepository.save(entityMapper.toEntity(reserva));

        log.info("Reserva creada: id={}, cliente={}, mesa={}",
                guardada.getId(), guardada.getCliente(), guardada.getIdMesa());
        return entityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        ReservaEntity entity = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva", id));

        Reserva reserva = entityMapper.toDomain(entity);
        validator.validarTransicion(reserva, nuevoEstado);

        entity.setEstado(nuevoEstado);
        log.info("Reserva #{} → {}", id, nuevoEstado);
        return entityMapper.toDomain(reservaRepository.save(entity));
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        ReservaEntity entity = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva", id));

        Reserva reserva = entityMapper.toDomain(entity);
        validator.validarCancelable(reserva);

        entity.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(entity);
        log.info("Reserva #{} cancelada", id);
    }
}