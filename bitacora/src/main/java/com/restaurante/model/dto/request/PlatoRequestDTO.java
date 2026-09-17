package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada. Solo validaciones de INPUT (Bean Validation).
 * Las reglas de negocio van en el Service / Validator.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;
}