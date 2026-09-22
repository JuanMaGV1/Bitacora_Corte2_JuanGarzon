package com.restaurante.exception;

public class CuentaNoAbiertaException extends RuntimeException {
    public CuentaNoAbiertaException(String mensaje) { super(mensaje); }
}