package com.restaurante.controller;

import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Tag(name = "Platos", description = "Gestión de la carta del restaurante")
public class PlatoController {

    private final PlatoService platoService;
    private final PlatoMapper  platoMapper;

    @GetMapping
    @Operation(summary = "Listar todos los platos")
    public ResponseEntity<List<PlatoResponseDTO>> listar() {
        return ResponseEntity.ok(platoMapper.toResponseList(platoService.obtenerTodos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un plato por ID")
    public ResponseEntity<PlatoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(platoMapper.toResponse(platoService.obtenerPorId(id)));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar platos por categoría")
    public ResponseEntity<List<PlatoResponseDTO>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(platoMapper.toResponseList(
                platoService.obtenerPorCategoria(categoria)));
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Crear un nuevo plato")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Plato creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<PlatoResponseDTO> crear(@RequestBody @Valid PlatoRequestDTO dto) {
        log.info("POST /api/v1/platos — nombre={}", dto.getNombre());
        Plato creado = platoService.crear(platoMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platoMapper.toResponse(creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')") 
    @Operation(summary = "Actualizar un plato completo")
    public ResponseEntity<PlatoResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid PlatoRequestDTO dto) {
        Plato actualizado = platoService.actualizar(id, platoMapper.toDomain(dto));
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/disponible")
    @PreAuthorize("hasAnyRole('GERENTE','MESERO')")
    @Operation(summary = "Cambiar disponibilidad de un plato")
    public ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible) {
        Plato actualizado = platoService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Eliminar un plato")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        platoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}