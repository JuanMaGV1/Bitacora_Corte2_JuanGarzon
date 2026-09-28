package com.restaurante.exception;

/**
 * Se lanza cuando se intenta agrupar en una tanda un plato que no es ROLL.
 * Regla característica de Sakura Sushi.
 */
public class NoEsRollException extends RuntimeException {
    public NoEsRollException(String mensaje) {
        super(mensaje);
    }
}