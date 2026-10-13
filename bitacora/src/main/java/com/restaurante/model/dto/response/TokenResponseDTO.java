package com.restaurante.model.dto.response;

import com.restaurante.model.domain.Rol;
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