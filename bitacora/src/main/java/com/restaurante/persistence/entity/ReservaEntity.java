package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idMesa;

    @Column(nullable = false, length = 80)
    private String cliente;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Integer comensales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;
}