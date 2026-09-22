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
 * MenuController — Vista del cliente sobre la carta de Sakura Sushi.
 * Solo lectura, solo rolls disponibles.
 * Reutiliza IPlatoService — NO duplica lógica.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menú", description = "Consulta de la carta de Sakura Sushi — vista del cliente")
public class MenuController {

    private final PlatoService platoService;
    private final PlatoMapper  platoMapper;

    @GetMapping
    @Operation(summary = "Ver la carta completa disponible")
    @ApiResponse(responseCode = "200", description = "Lista de rolls y platos disponibles")
    public ResponseEntity<List<PlatoResponseDTO>> verCarta() {
        log.info("GET /api/v1/menu — consultando carta");
        return ResponseEntity.ok(
                platoMapper.toResponseList(platoService.obtenerDisponibles())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de un plato del menú")
    public ResponseEntity<PlatoResponseDTO> verDetalle(@PathVariable Long id) {
        Plato plato = platoService.obtenerPorId(id);
        if (!plato.estaDisponible()) {
            throw new com.restaurante.exception.PlatoNotFoundException(
                    "Plato disponible", id);
        }
        return ResponseEntity.ok(platoMapper.toResponse(plato));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Ver la carta filtrada por categoría")
    public ResponseEntity<List<PlatoResponseDTO>> porCategoria(
            @PathVariable String categoria) {

        log.info("GET /api/v1/menu/categoria/{}", categoria);

        return ResponseEntity.ok(
                platoService.obtenerPorCategoria(categoria).stream()
                        .filter(Plato::estaDisponible)
                        .map(platoMapper::toResponse)
                        .toList()
        );
    }
}