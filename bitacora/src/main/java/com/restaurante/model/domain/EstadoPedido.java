package com.restaurante.model.domain;

/**
 * Estados de un pedido en Sakura Sushi.
 * Regla SS-06: solo avanza en orden, sin saltos, entregado es terminal.
 */
public enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO,
    ENTREGADO,
    CANCELADO;

    /**
     * Regla de negocio: ¿este estado puede pasar al siguiente?
     * Vive en el enum — es parte del dominio.
     */
    public boolean puedeTransicionarA(EstadoPedido siguiente) {
        return switch (this) {
            case RECIBIDO       -> siguiente == EN_PREPARACION || siguiente == CANCELADO;
            case EN_PREPARACION -> siguiente == LISTO;
            case LISTO          -> siguiente == ENTREGADO;
            default             -> false;  // ENTREGADO y CANCELADO son terminales
        };
    }

    public boolean esCancelable() {
        return this == RECIBIDO;
    }

    public boolean esTerminal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}