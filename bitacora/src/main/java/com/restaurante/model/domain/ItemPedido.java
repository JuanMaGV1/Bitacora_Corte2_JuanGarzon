package com.restaurante.model.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    private Long    idPlato;
    private String  nombrePlato;
    private Double  precioCongelado;
    private Integer cantidad;

    public double subtotal() {
        return precioCongelado * cantidad;
    }
}