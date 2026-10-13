package com.restaurante.model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngresoCategoriaDTO {
    private String categoria;
    private Double ingreso;
    private Long   cantidad;
}