package com.restaurante.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta uniforme para TODOS los errores del API.
 * El GlobalExceptionHandler la construye.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int           status;
    private String        error;
    private String        message;
    private String        path;
}