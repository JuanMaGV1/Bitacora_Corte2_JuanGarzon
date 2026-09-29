package com.restaurante.repository;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.persistence.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<MesaEntity, Long> {

    List<MesaEntity> findByEstado(EstadoMesa estado);
    Optional<MesaEntity> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
}