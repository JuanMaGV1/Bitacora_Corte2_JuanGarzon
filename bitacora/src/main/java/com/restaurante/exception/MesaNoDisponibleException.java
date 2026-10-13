package com.restaurante.exception;

/**
 * Se lanza cuando se intenta usar una mesa que no está disponible.
 */
public class MesaNoDisponibleException extends RuntimeException {
    public MesaNoDisponibleException(String mensaje) { super(mensaje); }
}