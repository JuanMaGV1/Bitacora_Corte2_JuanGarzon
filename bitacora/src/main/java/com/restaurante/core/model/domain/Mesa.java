package com.restaurante.core.model.domain;

import com.restaurante.core.model.domain.enums.EstadoMesa;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {
    private Long       id;
    private Integer    numero;
    private Integer    capacidad;
    private EstadoMesa estado;
    private Boolean    cuentaAbierta;

    public boolean estaDisponible()     { return estado == EstadoMesa.DISPONIBLE; }
    public boolean tieneCuentaAbierta() { return Boolean.TRUE.equals(cuentaAbierta); }
    public void abrirCuenta()  { this.cuentaAbierta = true;  this.estado = EstadoMesa.OCUPADA;    }
    public void cerrarCuenta() { this.cuentaAbierta = false; this.estado = EstadoMesa.DISPONIBLE; }
}