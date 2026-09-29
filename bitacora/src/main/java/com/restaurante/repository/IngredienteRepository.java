package com.restaurante.repository;

import com.restaurante.persistence.entity.IngredienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRepository extends JpaRepository<IngredienteEntity, Long> {

    List<IngredienteEntity> findByDisponibleTrue();
    List<IngredienteEntity> findByTipoIgnoreCase(String tipo);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}