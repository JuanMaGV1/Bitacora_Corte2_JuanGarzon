package com.restaurante.model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponseDTO {
    private Long    idPlato;
    private String  nombrePlato;
    private Double  precioCongelado;
    private Integer cantidad;
    private Double  subtotal;
}