package com.restaurante.core.model.domain.enums;

public enum EstadoCuenta {
    ABIERTA, EN_PAGO, CERRADA;

    public boolean estaAbierta() { return this == ABIERTA || this == EN_PAGO; }
    public boolean estaCerrada() { return this == CERRADA; }
}