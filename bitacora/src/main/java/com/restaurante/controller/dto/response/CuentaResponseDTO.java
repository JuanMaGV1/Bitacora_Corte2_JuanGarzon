package com.restaurante.controller.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.restaurante.core.model.domain.enums.EstadoCuenta;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponseDTO {
    private Long          id;
    private Long          idMesa;
    private List<Long>    idsPedidos;
    private Double        total;
    private EstadoCuenta  estado;
    private LocalDateTime fechaApertura;
}