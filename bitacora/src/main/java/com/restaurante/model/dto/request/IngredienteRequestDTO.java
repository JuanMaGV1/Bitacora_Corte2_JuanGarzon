package com.restaurante.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteRequestDTO {

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(min = 2, max = 60)
    private String nombre;

    @NotNull(message = "El precio adicional es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotBlank(message = "El tipo de ingrediente es obligatorio")
    private String tipo;
}