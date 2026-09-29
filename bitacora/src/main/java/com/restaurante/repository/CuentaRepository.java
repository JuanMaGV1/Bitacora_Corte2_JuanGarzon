package com.restaurante.repository;

import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.persistence.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    Optional<CuentaEntity> findByIdMesaAndEstadoIn(Long idMesa, List<EstadoCuenta> estados);
    List<CuentaEntity> findByEstadoIn(List<EstadoCuenta> estados);
}