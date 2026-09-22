package com.restaurante.exception;

public class IngredienteNotFoundException extends RuntimeException {
    public IngredienteNotFoundException(String mensaje) { super(mensaje); }
    public IngredienteNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}