package com.restaurante.exception;

public class ReservaConflictoException extends RuntimeException {
    public ReservaConflictoException(String mensaje) { super(mensaje); }
}