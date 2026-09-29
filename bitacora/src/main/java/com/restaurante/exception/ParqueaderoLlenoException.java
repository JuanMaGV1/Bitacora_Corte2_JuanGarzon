package com.restaurante.exception;

/**
 * Se lanza cuando el parqueadero está lleno o cuando una placa
 * ya tiene un registro activo.
 */
public class ParqueaderoLlenoException extends RuntimeException {

    public ParqueaderoLlenoException(String mensaje) {
        super(mensaje);
    }
}