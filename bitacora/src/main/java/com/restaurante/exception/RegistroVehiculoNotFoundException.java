package com.restaurante.exception;

/**
 * Se lanza cuando se busca un registro de vehículo que no existe,
 * o cuando no hay un registro activo para una placa.
 */
public class RegistroVehiculoNotFoundException extends RuntimeException {

    public RegistroVehiculoNotFoundException(String mensaje) {
        super(mensaje);
    }

    public RegistroVehiculoNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}