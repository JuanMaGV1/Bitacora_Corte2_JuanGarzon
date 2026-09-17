package com.restaurante.exception;

/**
 * Se lanza cuando se intenta crear un plato con un nombre que ya existe.
 * El GlobalExceptionHandler la traduce a un 409 Conflict.
 */
public class PlatoAlreadyExistsException extends RuntimeException {

    public PlatoAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}