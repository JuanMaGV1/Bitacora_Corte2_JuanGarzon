package com.restaurante.exception;

/**
 * Se lanza cuando se intenta usar un ingrediente agotado (SS-RNF-06).
 */
public class IngredienteAgotadoException extends RuntimeException {
    public IngredienteAgotadoException(String mensaje) { super(mensaje); }
}