package com.restaurante.model.domain;

public enum EstadoVehiculo {
    ACTIVO,      // dentro del parqueadero
    CERRADO;     // ya salió

    public boolean estaActivo() {
        return this == ACTIVO;
    }
}