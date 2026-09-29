package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA del plato. Representa la tabla "platos" en PostgreSQL.
 * NO tiene lógica de negocio — solo estructura y anotaciones JPA.
 */
@Entity
@Table(name = "platos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 60)
    private String categoria;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean disponible;

    @CreationTimestamp
    private LocalDateTime creadoEn;
}