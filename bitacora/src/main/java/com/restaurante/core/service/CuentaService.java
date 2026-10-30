package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.Cuenta;

public interface CuentaService {
    List<Cuenta> obtenerTodas();
    Cuenta obtenerPorId(Long id);
    Cuenta obtenerPorMesa(Long idMesa);
    Cuenta abrir(Long idMesa);
    Cuenta agregarPedido(Long idCuenta, Long idPedido);
    Cuenta cerrar(Long id);
}