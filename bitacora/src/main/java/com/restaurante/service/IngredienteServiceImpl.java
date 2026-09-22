package com.restaurante.service;

import com.restaurante.exception.IngredienteNotFoundException;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.validator.IngredienteValidator;
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
public class IngredienteServiceImpl implements IngredienteService {

    private final Map<Long, Ingrediente> ingredientes = new ConcurrentHashMap<>();
    private final AtomicLong             contador     = new AtomicLong(1);

    private final IngredienteValidator validator;

    @Override
    public List<Ingrediente> obtenerTodos() {
        log.debug("Obteniendo todos los ingredientes. Total={}", ingredientes.size());
        return ingredientes.values().stream().toList();
    }

    @Override
    public List<Ingrediente> obtenerDisponibles() {
        return ingredientes.values().stream()
                .filter(Ingrediente::estaDisponible)
                .toList();
    }

    @Override
    public List<Ingrediente> obtenerPorTipo(String tipo) {
        return ingredientes.values().stream()
                .filter(i -> i.getTipo().equalsIgnoreCase(tipo))
                .toList();
    }

    @Override
    public Ingrediente obtenerPorId(Long id) {
        return ingredientes.values().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Ingrediente no encontrado: id={}", id);
                    return new IngredienteNotFoundException("Ingrediente", id);
                });
    }

    @Override
    public Ingrediente crear(Ingrediente ingrediente) {
        validator.validarNombreUnico(ingrediente.getNombre(), ingredientes.values());

        ingrediente.setId(contador.getAndIncrement());
        ingrediente.setDisponible(true);
        ingredientes.put(ingrediente.getId(), ingrediente);

        log.info("Ingrediente creado: id={}, nombre={}",
                ingrediente.getId(), ingrediente.getNombre());
        return ingrediente;
    }

    @Override
    public Ingrediente cambiarDisponibilidad(Long id, boolean disponible) {
        Ingrediente ingrediente = obtenerPorId(id);
        if (disponible) ingrediente.activar(); else ingrediente.desactivar();
        log.info("Ingrediente id={} → disponible={}", id, disponible);
        return ingrediente;
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        ingredientes.remove(id);
        log.info("Ingrediente eliminado: id={}", id);
    }
}