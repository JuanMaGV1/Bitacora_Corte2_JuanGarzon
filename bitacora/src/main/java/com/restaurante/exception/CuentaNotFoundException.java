package com.restaurante.exception;

public class CuentaNotFoundException extends RuntimeException {
    public CuentaNotFoundException(String mensaje) { super(mensaje); }
    public CuentaNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}