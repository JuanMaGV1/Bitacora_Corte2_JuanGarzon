package com.restaurante.model.domain;

public enum EstadoMesa {
    DISPONIBLE,
    OCUPADA,
    RESERVADA;

    public boolean estaDisponible() {
        return this == DISPONIBLE;
    }

    public boolean estaOcupada() {
        return this == OCUPADA;
    }
}