package com.restaurante.model.dto.response;

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
public class TandaResponseDTO {
    private Long             id;
    private List<Long>       idsRolls;
    private Integer          cantidad;
    private LocalDateTime    fechaCreacion;
    private String           estado;
}