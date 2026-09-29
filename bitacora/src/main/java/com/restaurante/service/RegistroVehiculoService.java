package com.restaurante.service;

import com.restaurante.model.domain.RegistroVehiculo;

import java.util.List;

/**
 * Contrato del servicio de parqueadero.
 * Define QUÉ se puede hacer con los registros de vehículos,
 * sin especificar CÓMO se hace (SQL, en memoria, cache, etc.).
 *
 * Los consumidores (Controller, otros Services) solo conocen
 * esta interfaz, nunca la implementación concreta.
 */
public interface RegistroVehiculoService {

    /** Lista todos los registros (activos y cerrados). */
    List<RegistroVehiculo> obtenerTodos();

    /** Lista solo los vehículos que están actualmente dentro (salida == null). */
    List<RegistroVehiculo> obtenerActivos();

    /** Historial completo de una placa (todas sus visitas). */
    List<RegistroVehiculo> obtenerPorPlaca(String placa);

    /** Busca un registro por ID. */
    RegistroVehiculo obtenerPorId(Long id);

    /** Registra la entrada de un vehículo al parqueadero. */
    RegistroVehiculo registrarEntrada(RegistroVehiculo registro);

    /** Registra la salida y calcula el cobro final. */
    RegistroVehiculo registrarSalida(String placa);

    /** Cuántos cupos quedan disponibles. */
    int cuposDisponibles();
}