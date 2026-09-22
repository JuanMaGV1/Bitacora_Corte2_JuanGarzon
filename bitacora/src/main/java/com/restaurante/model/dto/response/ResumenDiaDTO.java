package com.restaurante.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenDiaDTO {
    private Long                 totalPedidos;
    private Double               ingresoTotal;
    private String               platoMasPedido;
    private Long                 mesasConCuentaAbierta;
    private Map<String, Long>    platosPopulares;
}