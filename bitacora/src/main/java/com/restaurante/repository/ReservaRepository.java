package com.restaurante.repository;

import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByClienteIgnoreCase(String cliente);
    List<ReservaEntity> findByEstadoIn(List<EstadoReserva> estados);

    /** Reservas activas de una mesa en una ventana de 2 horas */
    List<ReservaEntity> findByIdMesaAndEstadoInAndFechaHoraBetween(
            Long idMesa,
            List<EstadoReserva> estados,
            LocalDateTime desde,
            LocalDateTime hasta);
}