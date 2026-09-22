package com.restaurante.service;

import com.restaurante.model.domain.Cuenta;

import java.util.List;

public interface CuentaService {

    List<Cuenta> obtenerTodas();
    Cuenta obtenerPorId(Long id);
    Cuenta obtenerPorMesa(Long idMesa);
    Cuenta abrir(Long idMesa);
    Cuenta agregarPedido(Long idCuenta, Long idPedido);
    Cuenta cerrar(Long id);
}