package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idPlato;

    @Column(nullable = false, length = 120)
    private String nombrePlato;

    @Column(nullable = false)
    private Double precioCongelado;

    @Column(nullable = false)
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;
}