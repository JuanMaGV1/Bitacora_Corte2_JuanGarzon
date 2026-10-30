package com.restaurante.core.model.domain.enums;

public enum EstadoMesa {
    DISPONIBLE, OCUPADA, RESERVADA;

    public boolean estaDisponible() { return this == DISPONIBLE; }
    public boolean estaOcupada()    { return this == OCUPADA; }
}