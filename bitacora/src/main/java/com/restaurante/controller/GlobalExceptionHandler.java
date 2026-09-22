package com.restaurante.controller;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Intercepta TODAS las excepciones de los Controllers.
 * Devuelve siempre el mismo formato (ErrorResponseDTO).
 *
 * ⚠️ Orden de los handlers: de MÁS específico a MÁS general.
 *    El @ExceptionHandler(Exception.class) va SIEMPRE al final.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 400 — Validación de input (@Valid falló) ───────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        log.warn("Validación fallida: {}", mensaje);
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // ─── 404 — Recurso del dominio no encontrado ────────────────────────

    @ExceptionHandler(PlatoNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            PlatoNotFoundException ex, HttpServletRequest request) {
        log.warn("No encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ─── 409 — Conflicto (nombre duplicado) ─────────────────────────────

    @ExceptionHandler(PlatoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            PlatoAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Conflicto: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ─── 422 — Regla de negocio violada (precio fuera de rango, etc.) ───

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Regla de negocio: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // ─── 404 — Recursos estáticos (favicon.ico, robots.txt) ─────────────
    // Sin esto, el navegador pidiendo favicon genera un 500 feo.

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResource(
            NoResourceFoundException ex, HttpServletRequest request) {
        log.debug("Recurso estático no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
    }

    // ─── 404 — Rutas sin handler ────────────────────────────────────────

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandler(
            NoHandlerFoundException ex, HttpServletRequest request) {
        log.debug("Ruta sin handler: {}", ex.getRequestURL());
        return build(HttpStatus.NOT_FOUND, "Ruta no encontrada", request);
    }

    // ─── 500 — Cualquier otro error no previsto (SIEMPRE AL FINAL) ──────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error inesperado del servidor", request);
    }

    // ─── Helper: construye el ErrorResponseDTO ──────────────────────────

        @ExceptionHandler(PedidoNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePedidoNotFound(
            PedidoNotFoundException ex, HttpServletRequest request) {
        log.warn("Pedido no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEstadoInvalido(
            EstadoInvalidoException ex, HttpServletRequest request) {
        log.warn("Estado inválido: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
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
    // ─── Ingrediente: 404 ──────────────────────────────────────
    @ExceptionHandler(com.restaurante.exception.IngredienteNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleIngredienteNotFound(
            com.restaurante.exception.IngredienteNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Ingrediente no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ─── Ingrediente: 409 (nombre duplicado) ──────────────────
    @ExceptionHandler(com.restaurante.exception.IngredienteAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIngredienteConflict(
            com.restaurante.exception.IngredienteAlreadyExistsException ex,
            HttpServletRequest request) {
        log.warn("Conflicto de ingrediente: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ─── Ingrediente: 422 (agotado — regla SS-RNF-06) ─────────
    @ExceptionHandler(com.restaurante.exception.IngredienteAgotadoException.class)
    public ResponseEntity<ErrorResponseDTO> handleIngredienteAgotado(
            com.restaurante.exception.IngredienteAgotadoException ex,
            HttpServletRequest request) {
        log.warn("Ingrediente agotado: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }
    // ─── Reserva: 404 ───────────────────────────────────────────
    @ExceptionHandler(com.restaurante.exception.ReservaNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleReservaNotFound(
            com.restaurante.exception.ReservaNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Reserva no encontrada: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ─── Reserva: 409 (conflicto de horario) ───────────────────
    @ExceptionHandler(com.restaurante.exception.ReservaConflictoException.class)
    public ResponseEntity<ErrorResponseDTO> handleReservaConflicto(
            com.restaurante.exception.ReservaConflictoException ex,
            HttpServletRequest request) {
        log.warn("Conflicto de reserva: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }
    // ─── Mesa: 404 ──────────────────────────────────────────────
    @ExceptionHandler(com.restaurante.exception.MesaNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleMesaNotFound(
            com.restaurante.exception.MesaNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Mesa no encontrada: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ─── Mesa: 422 (estado no permite la operación) ─────────────
    @ExceptionHandler(com.restaurante.exception.MesaNoDisponibleException.class)
    public ResponseEntity<ErrorResponseDTO> handleMesaNoDisponible(
            com.restaurante.exception.MesaNoDisponibleException ex,
            HttpServletRequest request) {
        log.warn("Mesa no disponible: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }
    @ExceptionHandler(com.restaurante.exception.CuentaNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCuentaNotFound(
            com.restaurante.exception.CuentaNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Cuenta no encontrada: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(com.restaurante.exception.CuentaNoAbiertaException.class)
    public ResponseEntity<ErrorResponseDTO> handleCuentaNoAbierta(
            com.restaurante.exception.CuentaNoAbiertaException ex,
            HttpServletRequest request) {
        log.warn("Cuenta no abierta: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }
}