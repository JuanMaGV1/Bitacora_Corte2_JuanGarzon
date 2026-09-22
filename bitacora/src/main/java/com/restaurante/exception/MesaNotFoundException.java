package com.restaurante.exception;

public class MesaNotFoundException extends RuntimeException {
    public MesaNotFoundException(String mensaje) { super(mensaje); }
    public MesaNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}