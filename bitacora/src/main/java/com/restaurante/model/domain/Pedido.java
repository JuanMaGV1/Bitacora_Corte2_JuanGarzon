package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de Sakura Sushi. Contiene la lista de rolls/items.
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

    /** Total = suma de subtotales */
    public double calcularTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                .mapToDouble(ItemPedido::subtotal)
                .sum();
    }

    /** Solo se puede modificar si está en RECIBIDO (SS-02, SS-03, SS-04) */
    public boolean puedeModificarse() {
        return estado == EstadoPedido.RECIBIDO;
    }

    /** Cantidad de rolls/items — útil para SS-10 (tandas de 6) */
    public int cantidadItems() {
        return items == null ? 0 : items.size();
    }
}