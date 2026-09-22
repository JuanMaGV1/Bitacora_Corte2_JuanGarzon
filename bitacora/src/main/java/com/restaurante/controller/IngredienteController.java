package com.restaurante.controller;

import com.restaurante.mapper.IngredienteMapper;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.model.dto.request.IngredienteRequestDTO;
import com.restaurante.model.dto.response.IngredienteResponseDTO;
import com.restaurante.service.IngredienteService;
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
@RequestMapping("/api/v1/ingredientes")
@RequiredArgsConstructor
@Tag(name = "Ingredientes", description = "Ingredientes disponibles para personalizar rolls (SS-09)")
public class IngredienteController {

    private final IngredienteService ingredienteService;
    private final IngredienteMapper  ingredienteMapper;

    @GetMapping
    @Operation(summary = "Listar todos los ingredientes")
    public ResponseEntity<List<IngredienteResponseDTO>> listar() {
        return ResponseEntity.ok(
                ingredienteMapper.toResponseList(ingredienteService.obtenerTodos()));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar ingredientes disponibles")
    public ResponseEntity<List<IngredienteResponseDTO>> disponibles() {
        return ResponseEntity.ok(
                ingredienteMapper.toResponseList(ingredienteService.obtenerDisponibles()));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar ingredientes por tipo")
    public ResponseEntity<List<IngredienteResponseDTO>> porTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(
                ingredienteMapper.toResponseList(ingredienteService.obtenerPorTipo(tipo)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un ingrediente por ID")
    public ResponseEntity<IngredienteResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                ingredienteMapper.toResponse(ingredienteService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo ingrediente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ingrediente creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<IngredienteResponseDTO> crear(
            @RequestBody @Valid IngredienteRequestDTO dto) {

        log.info("POST /api/v1/ingredientes — nombre={}", dto.getNombre());
        Ingrediente creado = ingredienteService.crear(ingredienteMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredienteMapper.toResponse(creado));
    }

    @PatchMapping("/{id}/disponible")
    @Operation(summary = "Cambiar disponibilidad de un ingrediente (SS-RNF-06)")
    public ResponseEntity<IngredienteResponseDTO> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible) {
        Ingrediente actualizado = ingredienteService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok(ingredienteMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un ingrediente")
    @ApiResponse(responseCode = "204", description = "Ingrediente eliminado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingredienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}