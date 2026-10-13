package com.restaurante.model.domain;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroVehiculo {
    private Long          id;
    private String        placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double        cobro;

    public boolean estaActivo() { return salida == null; }
}