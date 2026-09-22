package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    private Long            id;
    private Long            idMesa;
    private String          cliente;
    private LocalDateTime   fechaHora;
    private Integer         comensales;
    private EstadoReserva   estado;

    public boolean estaVigente() {
        return estado != null && estado.estaVigente();
    }

    /** Regla de negocio: ¿esta reserva choca con otra en un rango de 2h? */
    public boolean chocaCon(Reserva otra) {
        if (otra == null) return false;
        long diffMinutos = Math.abs(
                java.time.Duration.between(this.fechaHora, otra.fechaHora).toMinutes());
        return diffMinutos < 120;
    }
}