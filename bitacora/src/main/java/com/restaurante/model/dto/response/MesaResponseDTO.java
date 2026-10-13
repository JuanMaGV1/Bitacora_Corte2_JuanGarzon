package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoMesa;
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