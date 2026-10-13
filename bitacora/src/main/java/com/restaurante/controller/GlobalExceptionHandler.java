package com.restaurante.controller;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 403 — Autenticado pero sin permisos
@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
        org.springframework.security.access.AccessDeniedException ex,
        HttpServletRequest request) {
    log.warn("Acceso denegado: {}", request.getRequestURI());
    return build(HttpStatus.FORBIDDEN,
            "No tienes permisos para esta acción", request);
}

// ── 401 — No autenticado
@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
public ResponseEntity<ErrorResponseDTO> handleAuthentication(
        org.springframework.security.core.AuthenticationException ex,
        HttpServletRequest request) {
    log.warn("No autenticado: {}", request.getRequestURI());
    return build(HttpStatus.UNAUTHORIZED,
            "Debes autenticarte primero", request);
}

    // ── 400 — Validación de input (@Valid falló)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        log.warn("Validación fallida: {}", mensaje);
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // ── 404 — Recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            RecursoNoEncontradoException ex, HttpServletRequest request) {
        log.warn("No encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ── 409 — Conflicto
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            ConflictoException ex, HttpServletRequest request) {
        log.warn("Conflicto: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ── 422 — Estado inválido / Regla de negocio
    @ExceptionHandler({EstadoInvalidoException.class, ReglaDeNegocioException.class,
                       IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> handleBusiness(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Regla de negocio: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // ── 404 — Recursos estáticos (favicon.ico)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResource(
            NoResourceFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
    }

    // ── 500 — Cualquier otro error (SIEMPRE AL FINAL)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error inesperado del servidor", request);
    }

    private ResponseEntity<ErrorResponseDTO> build(
            HttpStatus status, String message, HttpServletRequest request) {

        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(com.restaurante.exception.TandaExcedidaException.class)
    public ResponseEntity<ErrorResponseDTO> handleTandaExcedida(
            com.restaurante.exception.TandaExcedidaException ex, HttpServletRequest request) {
        log.warn("Tanda excedida: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(com.restaurante.exception.NoEsRollException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoEsRoll(
            com.restaurante.exception.NoEsRollException ex, HttpServletRequest request) {
        log.warn("No es roll: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }
}