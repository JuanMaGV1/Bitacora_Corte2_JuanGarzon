package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingredientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false)
    private Boolean disponible;

    @CreationTimestamp
    private LocalDateTime creadoEn;
}