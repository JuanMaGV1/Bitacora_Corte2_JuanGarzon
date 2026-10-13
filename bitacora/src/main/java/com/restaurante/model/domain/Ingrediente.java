package com.restaurante.model.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {
    private Long    id;
    private String  nombre;
    private Double  precio;
    private Boolean disponible;
    private String  tipo;

    public boolean estaDisponible() { return Boolean.TRUE.equals(disponible); }
    public void activar()    { this.disponible = true;  }
    public void desactivar() { this.disponible = false; }
}