package com.restaurante.controller.dto.response;

import com.restaurante.core.model.domain.enums.EstadoMesa;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaResponseDTO {
    private Long       id;
    private Integer    numero;
    private Integer    capacidad;
    private EstadoMesa estado;
    private Boolean    cuentaAbierta;
}