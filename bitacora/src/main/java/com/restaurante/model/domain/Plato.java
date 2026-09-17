package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de negocio pura.
 * Sin anotaciones de Spring ni JPA — solo Java + Lombok.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plato {

    private Long    id;
    private String  nombre;
    private Double  precio;
    private String  categoria;
    private String  descripcion;
    private Boolean disponible;

    /** Comportamiento propio del dominio: ¿puede pedirse este plato? */
    public boolean estaDisponible() {
        return Boolean.TRUE.equals(disponible);
    }

    public void activar()    { this.disponible = true;  }
    public void desactivar() { this.disponible = false; }
}