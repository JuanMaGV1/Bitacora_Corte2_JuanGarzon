package com.restaurante.exception;

public class IngredienteAlreadyExistsException extends RuntimeException {
    public IngredienteAlreadyExistsException(String mensaje) { super(mensaje); }
}