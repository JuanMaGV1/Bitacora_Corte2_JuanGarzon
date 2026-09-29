package com.restaurante.service;

import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository   platoRepository;
    private final PlatoEntityMapper entityMapper;
    private final PlatoValidator    validator;

    @Override
    public List<Plato> obtenerTodos() {
        log.debug("Obteniendo todos los platos de BD");
        return platoRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return platoRepository.findByDisponibleTrue().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Plato> obtenerPorCategoria(String categoria) {
        return platoRepository.findByCategoriaIgnoreCase(categoria).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Plato obtenerPorId(Long id) {
        return platoRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.warn("Plato no encontrado: id={}", id);
                    return new PlatoNotFoundException("Plato", id);
                });
    }

    @Override
    public Plato crear(Plato plato) {
        validator.validarNombreUnico(plato.getNombre());
        validator.validarPrecioRazonable(plato.getPrecio());

        plato.setDisponible(true);
        PlatoEntity guardado = platoRepository.save(entityMapper.toEntity(plato));

        log.info("Plato creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return entityMapper.toDomain(guardado);
    }

    @Override
    @Transactional
    public Plato actualizar(Long id, Plato nuevosDatos) {
        PlatoEntity existente = platoRepository.findById(id)
                .orElseThrow(() -> new PlatoNotFoundException("Plato", id));

        validator.validarNombreUnicoExcluyendo(nuevosDatos.getNombre(), id);
        validator.validarPrecioRazonable(nuevosDatos.getPrecio());

        existente.setNombre(nuevosDatos.getNombre());
        existente.setPrecio(nuevosDatos.getPrecio());
        existente.setCategoria(nuevosDatos.getCategoria());
        existente.setDescripcion(nuevosDatos.getDescripcion());

        log.info("Plato actualizado: id={}", id);
        return entityMapper.toDomain(platoRepository.save(existente));
    }

    @Override
    @Transactional
    public Plato cambiarDisponibilidad(Long id, boolean disponible) {
        PlatoEntity entity = platoRepository.findById(id)
                .orElseThrow(() -> new PlatoNotFoundException("Plato", id));
        entity.setDisponible(disponible);
        log.info("Plato id={} → disponible={}", id, disponible);
        return entityMapper.toDomain(platoRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new PlatoNotFoundException("Plato", id);
        }
        platoRepository.deleteById(id);
        log.info("Plato eliminado: id={}", id);
    }
}