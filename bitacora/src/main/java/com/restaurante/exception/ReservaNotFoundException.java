package com.restaurante.exception;

public class ReservaNotFoundException extends RuntimeException {
    public ReservaNotFoundException(String mensaje) { super(mensaje); }
    public ReservaNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}