package com.restaurante.model.domain;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    private Long          id;
    private Long          idMesa;
    private String        cliente;
    private LocalDateTime fechaHora;
    private Integer       comensales;
    private EstadoReserva estado;

    public boolean estaVigente() {
        return estado != null && estado.estaVigente();
    }

    public boolean chocaCon(Reserva otra) {
        if (otra == null) return false;
        long diffMinutos = Math.abs(Duration.between(this.fechaHora, otra.fechaHora).toMinutes());
        return diffMinutos < 120;
    }
}