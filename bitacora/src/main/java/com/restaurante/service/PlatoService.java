package com.restaurante.service;

import com.restaurante.model.domain.Plato;

import java.util.List;

/**
 * Contrato del servicio. Recibe y devuelve SIEMPRE objetos de dominio.
 * NUNCA DTOs — esos viven en la capa del Controller.
 */
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