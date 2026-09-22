package com.restaurante.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "El número de mesa es obligatorio")
    @Positive(message = "El número de mesa debe ser mayor a 0")
    private Long idMesa;

    @NotEmpty(message = "El pedido debe tener al menos un roll")
    private List<@NotNull(message = "El id del plato no puede ser nulo") Long> idPlatos;

    @Size(max = 200, message = "Las notas no pueden superar 200 caracteres")
    private String notas;
}