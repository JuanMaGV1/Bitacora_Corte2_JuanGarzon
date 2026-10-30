package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.RegistroVehiculo;

public interface RegistroVehiculoService {
    List<RegistroVehiculo> obtenerTodos();
    List<RegistroVehiculo> obtenerActivos();
    List<RegistroVehiculo> obtenerPorPlaca(String placa);
    RegistroVehiculo obtenerPorId(Long id);
    RegistroVehiculo registrarEntrada(RegistroVehiculo registro);
    RegistroVehiculo registrarSalida(String placa);
    int cuposDisponibles();
}