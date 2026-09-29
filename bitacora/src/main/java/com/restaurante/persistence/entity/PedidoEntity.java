package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Relación con Mesa — ManyToOne. Muchos pedidos pueden ser de la misma mesa. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private MesaEntity mesa;

    /** Relación con items — OneToMany con cascade ALL. */
    @OneToMany(
        mappedBy = "pedido",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ItemPedidoEntity> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(length = 200)
    private String notas;

    /** Helper: agrega item y setea la relación bidireccional */
    public void addItem(ItemPedidoEntity item) {
        items.add(item);
        item.setPedido(this);
    }
}