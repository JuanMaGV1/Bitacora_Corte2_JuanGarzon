package com.restaurante.controller;

import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MenuController — vista del cliente.
 * Solo lectura, solo platos disponibles.
 * Reutiliza IPlatoService — NO duplica lógica.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menú", description = "Consulta de la carta — vista del cliente")
public class MenuController {

    private final PlatoService platoService;
    private final PlatoMapper  platoMapper;

    // ─── GET /api/v1/menu — carta completa disponible ───────────────────

    @GetMapping
    @Operation(summary = "Ver la carta completa disponible")
    @ApiResponse(responseCode = "200", description = "Lista de platos disponibles")
    public ResponseEntity<List<PlatoResponseDTO>> verCarta() {
        log.info("GET /api/v1/menu");
        return ResponseEntity.ok(
                platoMapper.toResponseList(platoService.obtenerDisponibles())
        );
    }

    // ─── GET /api/v1/menu/{id} — detalle de un plato ────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de un plato disponible")
    @ApiResponse(responseCode = "200", description = "Plato encontrado y disponible")
    public ResponseEntity<PlatoResponseDTO> verDetalle(@PathVariable Long id) {
        Plato plato = platoService.obtenerPorId(id);
        // Si el plato existe pero no está disponible, lo tratamos como no visible
        if (!plato.estaDisponible()) {
            throw new com.restaurante.exception.PlatoNotFoundException(
                    "Plato disponible", id);
        }
        return ResponseEntity.ok(platoMapper.toResponse(plato));
    }

    // ─── GET /api/v1/menu/categoria/{categoria} — filtrar por categoría ─

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Ver la carta filtrada por categoría")
    @ApiResponse(responseCode = "200", description = "Platos disponibles de la categoría")
    public ResponseEntity<List<PlatoResponseDTO>> porCategoria(
            @PathVariable String categoria) {

        log.info("GET /api/v1/menu/categoria/{}", categoria);

        List<PlatoResponseDTO> platos = platoService.obtenerPorCategoria(categoria)
                .stream()
                .filter(Plato::estaDisponible)      // solo los que puede ver el cliente
                .map(platoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(platos);
    }
}