package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.Ingrediente;

public interface IngredienteService {
    List<Ingrediente> obtenerTodos();
    List<Ingrediente> obtenerDisponibles();
    List<Ingrediente> obtenerPorTipo(String tipo);
    Ingrediente obtenerPorId(Long id);
    Ingrediente crear(Ingrediente ingrediente);
    Ingrediente cambiarDisponibilidad(Long id, boolean disponible);
    void eliminar(Long id);
}