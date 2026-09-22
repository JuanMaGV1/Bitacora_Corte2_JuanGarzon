package com.restaurante.exception;

/**
 * Se lanza cuando se intenta modificar un pedido que ya no está en RECIBIDO (SS-02/03/04).
 */
public class PedidoNoModificableException extends RuntimeException {
    public PedidoNoModificableException(String mensaje) { super(mensaje); }
}