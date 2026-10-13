package com.restaurante.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TandaResponseDTO {
    private String        id;
    private List<Long>    idsRolls;
    private Integer       cantidad;
    private LocalDateTime fechaCreacion;
    private String        estado;
}