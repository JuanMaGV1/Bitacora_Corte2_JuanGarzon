package com.restaurante.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El número de mesa es obligatorio")
    @Positive(message = "El número de mesa debe ser mayor a 0")
    private Long idMesa;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 80)
    private String cliente;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La reserva debe ser en el futuro")
    private LocalDateTime fechaHora;

    @NotNull(message = "El número de comensales es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 comensal")
    @Max(value = 20, message = "Máximo 20 comensales por reserva")
    private Integer comensales;
}