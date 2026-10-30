package com.restaurante.controller.handlers.exception;

public class CuentaNoAbiertaException extends RuntimeException {
    public CuentaNoAbiertaException(String mensaje) { super(mensaje); }
}