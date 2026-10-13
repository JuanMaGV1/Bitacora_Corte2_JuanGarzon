package com.restaurante.service;

import com.restaurante.model.domain.RegistroVehiculo;

import java.util.List;

public interface RegistroVehiculoService {
    List<RegistroVehiculo> obtenerTodos();
    List<RegistroVehiculo> obtenerActivos();
    List<RegistroVehiculo> obtenerPorPlaca(String placa);
    RegistroVehiculo obtenerPorId(Long id);
    RegistroVehiculo registrarEntrada(RegistroVehiculo registro);
    RegistroVehiculo registrarSalida(String placa);
    int cuposDisponibles();
}