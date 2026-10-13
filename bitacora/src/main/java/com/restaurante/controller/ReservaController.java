package com.restaurante.controller;

import com.restaurante.mapper.ReservaMapper;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return ResponseEntity.ok(reservaMapper.toResponseList(reservaService.obtenerTodas()));
    }

    @GetMapping("/vigentes")
    @Operation(summary = "Listar reservas vigentes")
    public ResponseEntity<List<ReservaResponseDTO>> vigentes() {
        return ResponseEntity.ok(reservaMapper.toResponseList(reservaService.obtenerVigentes()));
    }

    @GetMapping("/cliente/{cliente}")
    @Operation(summary = "Listar reservas de un cliente")
    public ResponseEntity<List<ReservaResponseDTO>> porCliente(@PathVariable String cliente) {
        return ResponseEntity.ok(reservaMapper.toResponseList(reservaService.obtenerPorCliente(cliente)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por ID")
    public ResponseEntity<ReservaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(reservaMapper.toResponse(reservaService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MESERO','GERENTE','CLIENTE')")
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservaResponseDTO> crear(@RequestBody @Valid ReservaRequestDTO dto) {
        Reserva creada = reservaService.crear(reservaMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaMapper.toResponse(creada));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una reserva")
    public ResponseEntity<ReservaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoReserva nuevoEstado) {
        return ResponseEntity.ok(reservaMapper.toResponse(
                reservaService.cambiarEstado(id, nuevoEstado)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MESERO','GERENTE','CLIENTE')")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}