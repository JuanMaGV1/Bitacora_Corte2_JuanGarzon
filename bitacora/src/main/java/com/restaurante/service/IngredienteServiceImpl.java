package com.restaurante.service;

import com.restaurante.exception.IngredienteNotFoundException;
import com.restaurante.mapper.IngredienteEntityMapper;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.persistence.entity.IngredienteEntity;
import com.restaurante.repository.IngredienteRepository;
import com.restaurante.validator.IngredienteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredienteServiceImpl implements IngredienteService {

    private final IngredienteRepository    ingredienteRepository;
    private final IngredienteEntityMapper  entityMapper;
    private final IngredienteValidator     validator;

    @Override
    public List<Ingrediente> obtenerTodos() {
        return ingredienteRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Ingrediente> obtenerDisponibles() {
        return ingredienteRepository.findByDisponibleTrue().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Ingrediente> obtenerPorTipo(String tipo) {
        return ingredienteRepository.findByTipoIgnoreCase(tipo).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Ingrediente obtenerPorId(Long id) {
        return ingredienteRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new IngredienteNotFoundException("Ingrediente", id));
    }

    @Override
    public Ingrediente crear(Ingrediente ingrediente) {
        validator.validarNombreUnico(ingrediente.getNombre());

        ingrediente.setDisponible(true);
        IngredienteEntity guardado = ingredienteRepository.save(entityMapper.toEntity(ingrediente));

        log.info("Ingrediente creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return entityMapper.toDomain(guardado);
    }

    @Override
    @Transactional
    public Ingrediente cambiarDisponibilidad(Long id, boolean disponible) {
        IngredienteEntity entity = ingredienteRepository.findById(id)
                .orElseThrow(() -> new IngredienteNotFoundException("Ingrediente", id));
        entity.setDisponible(disponible);
        log.info("Ingrediente id={} → disponible={}", id, disponible);
        return entityMapper.toDomain(ingredienteRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new IngredienteNotFoundException("Ingrediente", id);
        }
        ingredienteRepository.deleteById(id);
        log.info("Ingrediente eliminado: id={}", id);
    }
}