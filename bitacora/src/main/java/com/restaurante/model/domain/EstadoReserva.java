package com.restaurante.model.domain;

public enum EstadoReserva {
    PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA;

    public boolean puedeTransicionarA(EstadoReserva siguiente) {
        return switch (this) {
            case PENDIENTE  -> siguiente == CONFIRMADA || siguiente == CANCELADA;
            case CONFIRMADA -> siguiente == COMPLETADA || siguiente == CANCELADA;
            default         -> false;
        };
    }

    public boolean esCancelable() { return this == PENDIENTE || this == CONFIRMADA; }
    public boolean estaVigente()  { return this == PENDIENTE || this == CONFIRMADA; }
}