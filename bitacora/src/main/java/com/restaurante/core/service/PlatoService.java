package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.Plato;

public interface PlatoService {
    List<Plato> obtenerTodos();
    List<Plato> obtenerDisponibles();
    List<Plato> obtenerPorCategoria(String categoria);
    Plato obtenerPorId(Long id);
    Plato crear(Plato plato);
    Plato actualizar(Long id, Plato nuevosDatos);
    Plato cambiarDisponibilidad(Long id, boolean disponible);
    void eliminar(Long id);
}