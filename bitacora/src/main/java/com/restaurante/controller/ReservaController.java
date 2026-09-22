package com.restaurante.controller;

import com.restaurante.mapper.ReservaMapper;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas del restaurante")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaMapper  reservaMapper;

    @GetMapping
    @Operation(summary = "Listar todas las reservas")
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        return ResponseEntity.ok(
                reservaMapper.toResponseList(reservaService.obtenerTodas()));
    }

    @GetMapping("/vigentes")
    @Operation(summary = "Listar reservas vigentes (pendientes o confirmadas)")
    public ResponseEntity<List<ReservaResponseDTO>> vigentes() {
        return ResponseEntity.ok(
                reservaMapper.toResponseList(reservaService.obtenerVigentes()));
    }

    @GetMapping("/cliente/{cliente}")
    @Operation(summary = "Listar reservas de un cliente")
    public ResponseEntity<List<ReservaResponseDTO>> porCliente(@PathVariable String cliente) {
        return ResponseEntity.ok(
                reservaMapper.toResponseList(reservaService.obtenerPorCliente(cliente)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por ID")
    public ResponseEntity<ReservaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                reservaMapper.toResponse(reservaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto de horario")
    })
    public ResponseEntity<ReservaResponseDTO> crear(
            @RequestBody @Valid ReservaRequestDTO dto) {

        log.info("POST /api/v1/reservas — cliente={}, mesa={}",
                dto.getCliente(), dto.getIdMesa());

        Reserva creada = reservaService.crear(reservaMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaMapper.toResponse(creada));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Reserva no existe"),
        @ApiResponse(responseCode = "422", description = "Transición inválida")
    })
    public ResponseEntity<ReservaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoReserva nuevoEstado) {

        Reserva actualizada = reservaService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(reservaMapper.toResponse(actualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reserva")
    @ApiResponse(responseCode = "204", description = "Reserva cancelada")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}