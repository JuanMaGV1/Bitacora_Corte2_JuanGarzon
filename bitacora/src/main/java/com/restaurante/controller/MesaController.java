package com.restaurante.controller;

import com.restaurante.mapper.MesaMapper;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
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
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Gestión de mesas del salón")
public class MesaController {

    private final MesaService mesaService;
    private final MesaMapper  mesaMapper;

    @GetMapping
    @Operation(summary = "Listar todas las mesas")
    public ResponseEntity<List<MesaResponseDTO>> listar() {
        return ResponseEntity.ok(
                mesaMapper.toResponseList(mesaService.obtenerTodas()));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar mesas disponibles")
    public ResponseEntity<List<MesaResponseDTO>> disponibles() {
        return ResponseEntity.ok(
                mesaMapper.toResponseList(mesaService.obtenerDisponibles()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una mesa por ID")
    public ResponseEntity<MesaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mesaMapper.toResponse(mesaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mesa creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Número duplicado")
    })
    public ResponseEntity<MesaResponseDTO> crear(
            @RequestBody @Valid MesaRequestDTO dto) {

        log.info("POST /api/v1/mesas — número={}", dto.getNumero());
        Mesa creada = mesaService.crear(mesaMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mesaMapper.toResponse(creada));
    }

    @PatchMapping("/{id}/abrir")
    @Operation(summary = "Abrir cuenta en una mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cuenta abierta"),
        @ApiResponse(responseCode = "404", description = "Mesa no existe"),
        @ApiResponse(responseCode = "422", description = "Mesa ya tiene cuenta abierta")
    })
    public ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(mesaMapper.toResponse(mesaService.abrirCuenta(id)));
    }

    @PatchMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar cuenta de una mesa")
    public ResponseEntity<MesaResponseDTO> cerrarCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(mesaMapper.toResponse(mesaService.cerrarCuenta(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una mesa (solo si no tiene cuenta abierta)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}