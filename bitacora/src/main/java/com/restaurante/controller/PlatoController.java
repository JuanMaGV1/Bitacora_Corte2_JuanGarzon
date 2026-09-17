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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * El Controller NO tiene lógica de negocio.
 * Solo: recibe → MapperIn → Service → MapperOut → responde.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Tag(name = "Platos", description = "Gestión de la carta del restaurante")
public class PlatoController {

    // ✅ Mapper inyectado en el Controller — no en el Service
    private final PlatoService platoService;
    private final PlatoMapper  platoMapper;

    // ─── GET /api/v1/platos ─────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todos los platos")
    @ApiResponse(responseCode = "200", description = "Lista devuelta correctamente")
    public ResponseEntity<List<PlatoResponseDTO>> listar() {
        log.info("GET /api/v1/platos");
        List<Plato> platos = platoService.obtenerTodos();
        return ResponseEntity.ok(platoMapper.toResponseList(platos));
    }

    // ─── GET /api/v1/platos/{id} ────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un plato por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plato encontrado"),
        @ApiResponse(responseCode = "404", description = "Plato no existe")
    })
    public ResponseEntity<PlatoResponseDTO> obtener(@PathVariable Long id) {
        Plato plato = platoService.obtenerPorId(id);
        return ResponseEntity.ok(platoMapper.toResponse(plato));
    }

    // ─── GET /api/v1/platos/categoria/{categoria} ───────────────────────

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar platos por categoría")
    @ApiResponse(responseCode = "200", description = "Platos de la categoría")
    public ResponseEntity<List<PlatoResponseDTO>> porCategoria(
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                platoMapper.toResponseList(platoService.obtenerPorCategoria(categoria))
        );
    }

    // ─── POST /api/v1/platos ────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Crear un nuevo plato")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Plato creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<PlatoResponseDTO> crear(
            @RequestBody @Valid PlatoRequestDTO dto) {

        log.info("POST /api/v1/platos — nombre={}", dto.getNombre());

        Plato plato      = platoMapper.toDomain(dto);        // MapperIn
        Plato creado     = platoService.crear(plato);        // Service
        PlatoResponseDTO response = platoMapper.toResponse(creado); // MapperOut

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── PUT /api/v1/platos/{id} ────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un plato completo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plato actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Plato no existe"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<PlatoResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid PlatoRequestDTO dto) {

        log.info("PUT /api/v1/platos/{}", id);
        Plato actualizado = platoService.actualizar(id, platoMapper.toDomain(dto));
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    // ─── PATCH /api/v1/platos/{id}/disponible ───────────────────────────

    @PatchMapping("/{id}/disponible")
    @Operation(summary = "Cambiar disponibilidad de un plato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
        @ApiResponse(responseCode = "404", description = "Plato no existe")
    })
    public ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible) {

        log.info("PATCH /api/v1/platos/{}/disponible — {}", id, disponible);
        Plato actualizado = platoService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    // ─── DELETE /api/v1/platos/{id} ─────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un plato")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Plato eliminado"),
        @ApiResponse(responseCode = "404", description = "Plato no existe")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/v1/platos/{}", id);
        platoService.eliminar(id);
        return ResponseEntity.noContent().build();   // 204 sin body
    }
}