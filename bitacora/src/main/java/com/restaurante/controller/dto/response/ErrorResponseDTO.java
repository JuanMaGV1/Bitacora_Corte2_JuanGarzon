package com.restaurante.controller.dto.response;

import lombok.*;

import java.time.LocalDateTime;

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