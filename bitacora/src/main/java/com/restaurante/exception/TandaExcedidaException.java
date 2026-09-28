package com.restaurante.exception;

/**
 * Se lanza cuando se intenta crear una tanda con más de 6 rolls.
 * Regla característica de Sakura Sushi (SS-10).
 */
public class TandaExcedidaException extends RuntimeException {
    public TandaExcedidaException(String mensaje) {
        super(mensaje);
    }
}