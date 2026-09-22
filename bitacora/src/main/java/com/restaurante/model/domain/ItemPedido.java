package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item de un pedido. Congela el precio del plato en el momento del pedido.
 * NO sigue el precio actual del plato (regla de Sakura).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    private Long    idPlato;
    private String  nombrePlato;
    private Double  precioCongelado;
    private Integer cantidad;

    /** Subtotal = precio congelado × cantidad */
    public double subtotal() {
        return precioCongelado * cantidad;
    }
}