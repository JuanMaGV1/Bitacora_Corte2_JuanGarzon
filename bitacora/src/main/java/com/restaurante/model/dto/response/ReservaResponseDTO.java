package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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