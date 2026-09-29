package com.restaurante.repository;

import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroVehiculoRepository extends JpaRepository<RegistroVehiculoEntity, Long> {

    List<RegistroVehiculoEntity> findByPlacaIgnoreCase(String placa);
    Optional<RegistroVehiculoEntity> findFirstByPlacaIgnoreCaseAndSalidaIsNull(String placa);
    boolean existsByPlacaIgnoreCaseAndSalidaIsNull(String placa);

    /** Vehículos activos = salida es null */
    @Query("SELECT r FROM RegistroVehiculoEntity r WHERE r.salida IS NULL")
    List<RegistroVehiculoEntity> findActivos();

    /** Cuenta activos para validar capacidad */
    @Query("SELECT COUNT(r) FROM RegistroVehiculoEntity r WHERE r.salida IS NULL")
    long countActivos();
}