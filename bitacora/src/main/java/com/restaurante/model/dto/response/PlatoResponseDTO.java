package com.restaurante.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida. Solo los campos que el cliente necesita ver.
 * Sin lógica, sin anotaciones de validación.
 */
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