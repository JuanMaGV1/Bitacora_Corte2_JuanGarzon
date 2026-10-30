package com.restaurante.controller.handlers.exception;

public class NoEsRollException extends RuntimeException {
    public NoEsRollException(String mensaje) { super(mensaje); }
}