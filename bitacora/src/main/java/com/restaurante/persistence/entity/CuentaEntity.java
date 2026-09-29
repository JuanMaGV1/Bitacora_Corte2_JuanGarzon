package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoCuenta;
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
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idMesa;

    /** Lista de IDs de pedidos que agrupa esta cuenta */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "cuenta_pedidos",
        joinColumns = @JoinColumn(name = "cuenta_id")
    )
    @Column(name = "pedido_id", nullable = false)
    @Builder.Default
    private List<Long> idsPedidos = new ArrayList<>();

    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaApertura;
}