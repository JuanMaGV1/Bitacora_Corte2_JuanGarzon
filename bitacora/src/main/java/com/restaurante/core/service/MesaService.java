package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.Mesa;

public interface MesaService {
    List<Mesa> obtenerTodas();
    List<Mesa> obtenerDisponibles();
    Mesa obtenerPorId(Long id);
    Mesa crear(Mesa mesa);
    Mesa abrirCuenta(Long id);
    Mesa cerrarCuenta(Long id);
    void eliminar(Long id);
}