package com.restaurante.controller.dto.response;

import lombok.*;

import java.time.LocalDateTime;

import com.restaurante.core.model.domain.enums.EstadoReserva;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long          id;
    private Long          idMesa;
    private String        cliente;
    private LocalDateTime fechaHora;
    private Integer       comensales;
    private EstadoReserva estado;
}