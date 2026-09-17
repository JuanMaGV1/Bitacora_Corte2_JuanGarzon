package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long                     id;
    private Long                     idMesa;
    private List<ItemPedidoResponseDTO> items;
    private EstadoPedido             estado;
    private LocalDateTime            timestamp;
    private Double                   total;
    private String                   notas;
}