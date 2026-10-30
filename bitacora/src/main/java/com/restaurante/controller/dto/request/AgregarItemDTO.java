package com.restaurante.controller.dto.request;




import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarItemDTO {

    @NotNull(message = "El id del plato es obligatorio")
    @Positive
    private Long idPlato;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}