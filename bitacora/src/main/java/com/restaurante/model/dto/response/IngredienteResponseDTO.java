package com.restaurante.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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