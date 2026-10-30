package com.restaurante.controller.handlers.exception;

/**
 * Se lanza cuando se intenta una transición de estado inválida (SS-06).
 */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String mensaje) { super(mensaje); }
}