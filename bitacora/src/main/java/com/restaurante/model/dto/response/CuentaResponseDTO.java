package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponseDTO {
    private Long           id;
    private Long           idMesa;
    private List<Long>     idsPedidos;
    private Double         total;
    private EstadoCuenta   estado;
    private LocalDateTime  fechaApertura;
}