package com.restaurante.model.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long   id;
    private String email;
    private String password;
    private String nombre;
    private Rol    rol;
}