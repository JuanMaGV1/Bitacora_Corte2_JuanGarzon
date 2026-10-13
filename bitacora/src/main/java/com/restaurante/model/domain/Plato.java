package com.restaurante.model.domain;

import lombok.*;

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

    public boolean estaDisponible() { return Boolean.TRUE.equals(disponible); }
    public void activar()    { this.disponible = true;  }
    public void desactivar() { this.disponible = false; }
}