package com.restaurante.service;

import com.restaurante.model.domain.Mesa;

import java.util.List;

public interface MesaService {

    List<Mesa> obtenerTodas();
    List<Mesa> obtenerDisponibles();
    Mesa obtenerPorId(Long id);
    Mesa obtenerPorNumero(Integer numero);
    Mesa crear(Mesa mesa);
    Mesa abrirCuenta(Long id);
    Mesa cerrarCuenta(Long id);
    void eliminar(Long id);
}