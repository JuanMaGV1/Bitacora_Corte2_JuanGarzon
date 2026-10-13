package com.restaurante.model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteResponseDTO {
    private Long    id;
    private String  nombre;
    private Double  precio;
    private String  tipo;
    private Boolean disponible;
}