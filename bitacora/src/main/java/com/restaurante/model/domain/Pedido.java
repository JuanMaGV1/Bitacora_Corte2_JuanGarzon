package com.restaurante.model.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long             id;
    private Long             idMesa;
    private List<ItemPedido> items;
    private EstadoPedido     estado;
    private LocalDateTime    timestamp;
    private String           notas;

    public double calcularTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream().mapToDouble(ItemPedido::subtotal).sum();
    }

    public boolean puedeModificarse() { return estado == EstadoPedido.RECIBIDO; }
    public int cantidadItems() { return items == null ? 0 : items.size(); }
}