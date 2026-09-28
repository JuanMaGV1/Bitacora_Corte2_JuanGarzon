package com.restaurante.controller;

import com.restaurante.model.dto.response.TandaResponseDTO;
import com.restaurante.service.TandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tandas")
@RequiredArgsConstructor
@Tag(name = "Tandas",
     description = "Preparación por tandas de máximo 6 rolls — regla característica de Sakura Sushi")
public class TandaController {

    private final TandaService tandaService;

    @PostMapping
    @Operation(summary = "Crear una nueva tanda de rolls (máx 6)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tanda creada"),
        @ApiResponse(responseCode = "404", description = "Algún id de roll no existe"),
        @ApiResponse(responseCode = "422",
                    description = "Más de 6 rolls, algún plato no es ROLL, o algún roll está agotado")
    })
    public ResponseEntity<TandaResponseDTO> crear(
            @RequestBody @NotEmpty List<Long> idsRolls) {

        log.info("POST /api/v1/tandas — {} rolls", idsRolls.size());
        TandaResponseDTO tanda = tandaService.crear(idsRolls);
        return ResponseEntity.status(HttpStatus.CREATED).body(tanda);
    }

    @GetMapping
    @Operation(summary = "Listar todas las tandas")
    public ResponseEntity<List<TandaResponseDTO>> listar() {
        return ResponseEntity.ok(tandaService.obtenerTodas());
    }
}