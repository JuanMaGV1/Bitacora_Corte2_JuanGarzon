package com.restaurante.model.domain;

/**
 * Estados posibles de un pedido.
 * El enum sabe qué transiciones son válidas — nadie más.
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
            case RECIBIDO        -> siguiente == EN_PREPARACION || siguiente == CANCELADO;
            case EN_PREPARACION  -> siguiente == LISTO;
            case LISTO           -> siguiente == ENTREGADO;
            default              -> false;
        };
    }

    public boolean esCancelable() {
        return this == RECIBIDO;
    }
}