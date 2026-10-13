package com.restaurante.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroVehiculoRequestDTO {

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(
        regexp = "^[A-Z]{3}-?[0-9]{3}$|^[A-Z]{3}[0-9]{2}[A-Z]$",
        message = "Formato de placa inválido (ej: ABC-123 o ABC12D)"
    )
    private String placa;
}