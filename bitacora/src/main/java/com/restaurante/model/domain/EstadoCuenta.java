package com.restaurante.model.domain;

public enum EstadoCuenta {
    ABIERTA,
    EN_PAGO,
    CERRADA;

    public boolean estaAbierta() {
        return this == ABIERTA || this == EN_PAGO;
    }

    public boolean estaCerrada() {
        return this == CERRADA;
    }
}