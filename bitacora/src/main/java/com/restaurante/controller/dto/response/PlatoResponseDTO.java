package com.restaurante.controller.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatoResponseDTO {
    private Long    id;
    private String  nombre;
    private Double  precio;
    private String  categoria;
    private String  descripcion;
    private Boolean disponible;
}