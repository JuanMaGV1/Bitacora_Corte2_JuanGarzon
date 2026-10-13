package com.restaurante.model.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    private Long          id;
    private Long          idMesa;
    private List<Long>    idsPedidos;
    private Double        total;
    private EstadoCuenta  estado;
    private LocalDateTime fechaApertura;

    public void agregarPedido(Long idPedido) {
        if (idsPedidos == null) idsPedidos = new ArrayList<>();
        idsPedidos.add(idPedido);
    }

    public void cerrarCuenta() { this.estado = EstadoCuenta.CERRADA; }
    public boolean estaAbierta() {
        return estado != null && estado.estaAbierta();
    }
}