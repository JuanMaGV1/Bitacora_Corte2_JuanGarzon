package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoReserva;
import lombok.*;

import java.time.LocalDateTime;

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