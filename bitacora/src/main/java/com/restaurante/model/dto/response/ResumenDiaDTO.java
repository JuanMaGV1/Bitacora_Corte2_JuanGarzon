package com.restaurante.model.dto.response;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenDiaDTO {
    private Long              totalPedidos;
    private Double            ingresoTotal;
    private String            platoMasPedido;
    private Long              mesasConCuentaAbierta;
    private Map<String, Long> platosPopulares;
}