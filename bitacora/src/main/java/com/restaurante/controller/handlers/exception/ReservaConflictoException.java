package com.restaurante.controller.handlers.exception;

public class ReservaConflictoException extends RuntimeException {
    public ReservaConflictoException(String mensaje) { super(mensaje); }
}