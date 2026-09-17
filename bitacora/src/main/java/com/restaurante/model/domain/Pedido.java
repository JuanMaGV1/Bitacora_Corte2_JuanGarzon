package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido del restaurante. Contiene la lista de items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    private Long                id;
    private Long                idMesa;
    private List<ItemPedido>    items;
    private EstadoPedido        estado;
    private LocalDateTime       timestamp;
    private String              notas;

    /** Suma de todos los subtotales */
    public double calcularTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                .mapToDouble(ItemPedido::subtotal)
                .sum();
    }

    /** Solo se puede modificar si está en RECIBIDO */
    public boolean puedeModificarse() {
        return estado == EstadoPedido.RECIBIDO;
    }
}