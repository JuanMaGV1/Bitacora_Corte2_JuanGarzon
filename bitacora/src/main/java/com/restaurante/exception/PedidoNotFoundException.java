package com.restaurante.exception;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(String mensaje) { super(mensaje); }
    public PedidoNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}