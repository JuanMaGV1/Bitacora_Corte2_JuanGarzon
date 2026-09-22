package com.restaurante.service;

import com.restaurante.model.domain.Ingrediente;

import java.util.List;

public interface IngredienteService {

    List<Ingrediente> obtenerTodos();
    List<Ingrediente> obtenerDisponibles();
    List<Ingrediente> obtenerPorTipo(String tipo);
    Ingrediente obtenerPorId(Long id);
    Ingrediente crear(Ingrediente ingrediente);
    Ingrediente cambiarDisponibilidad(Long id, boolean disponible);
    void eliminar(Long id);
}