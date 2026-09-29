package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoMesa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesa estado;

    @Column(nullable = false)
    private Boolean cuentaAbierta;
}