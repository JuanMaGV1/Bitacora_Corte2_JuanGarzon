package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ingrediente que el cliente puede escoger para personalizar un roll (SS-09).
 * Cada ingrediente tiene un precio adicional que suma al precio base del roll.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {

    private Long    id;
    private String  nombre;
    private Double  precio;      // precio adicional
    private Boolean disponible;
    private String  tipo;        // PESCADO, VERDURA, SALSA, etc.

    public boolean estaDisponible() {
        return Boolean.TRUE.equals(disponible);
    }

    public void activar()    { this.disponible = true;  }
    public void desactivar() { this.disponible = false; }
}