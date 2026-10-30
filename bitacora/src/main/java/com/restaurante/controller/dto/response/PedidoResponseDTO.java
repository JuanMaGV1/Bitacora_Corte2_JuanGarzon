package com.restaurante.controller.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.restaurante.core.model.domain.enums.EstadoPedido;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long                        id;
    private Long                        idMesa;
    private List<ItemPedidoResponseDTO> items;
    private EstadoPedido                estado;
    private LocalDateTime               timestamp;
    private Double                      total;
    private String                      notas;
}