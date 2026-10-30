package com.restaurante.controller.dto.response;

import com.restaurante.core.model.domain.enums.Rol;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private String token;
    private String email;
    private String nombre;
    private Rol    rol;
}