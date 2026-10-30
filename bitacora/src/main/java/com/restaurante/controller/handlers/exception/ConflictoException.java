package com.restaurante.controller.handlers.exception;

public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) { super(mensaje); }
}