package com.restaurante.controller;

import com.restaurante.mapper.RegistroVehiculoMapper;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.service.RegistroVehiculoService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/parqueadero")
@RequiredArgsConstructor
@Tag(name = "Parqueadero", description = "Registro de entrada y salida de vehículos")
public class ParqueaderoController {

    private final RegistroVehiculoService registroService;
    private final RegistroVehiculoMapper  registroMapper;

    // ─── POST /entrada ──────────────────────────────────────────────

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de un vehículo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Entrada registrada"),
        @ApiResponse(responseCode = "400", description = "Placa inválida"),
        @ApiResponse(responseCode = "409", description = "Placa ya registrada o parqueadero lleno")
    })
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarEntrada(
            @RequestBody @Valid RegistroVehiculoRequestDTO dto) {

        log.info("POST /api/v1/parqueadero/entrada — placa={}", dto.getPlaca());

        RegistroVehiculo creado = registroService.registrarEntrada(
                registroMapper.toDomain(dto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registroMapper.toResponse(creado));
    }

    // ─── POST /salida/{placa} ───────────────────────────────────────

    @PostMapping("/salida/{placa}")
    @Operation(summary = "Registrar salida y calcular el cobro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Salida registrada"),
        @ApiResponse(responseCode = "404", description = "No hay registro activo para esa placa")
    })
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarSalida(
            @PathVariable String placa) {

        log.info("POST /api/v1/parqueadero/salida/{}", placa);

        RegistroVehiculo cerrado = registroService.registrarSalida(placa);
        return ResponseEntity.ok(registroMapper.toResponse(cerrado));
    }

    // ─── GET /activos ───────────────────────────────────────────────

    @GetMapping("/activos")
    @Operation(summary = "Listar vehículos activos en el parqueadero")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> activos() {
        return ResponseEntity.ok(
                registroMapper.toResponseList(registroService.obtenerActivos()));
    }

    // ─── GET /estado ────────────────────────────────────────────────

    @GetMapping("/estado")
    @Operation(summary = "Consultar cupos disponibles y vehículos activos")
    public ResponseEntity<Map<String, Object>> estado() {
        return ResponseEntity.ok(Map.of(
                "cuposDisponibles", registroService.cuposDisponibles(),
                "vehiculosActivos", registroService.obtenerActivos().size()
        ));
    }

    // ─── GET /placa/{placa} ─────────────────────────────────────────

    @GetMapping("/placa/{placa}")
    @Operation(summary = "Historial de registros de una placa")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> porPlaca(
            @PathVariable String placa) {
        return ResponseEntity.ok(
                registroMapper.toResponseList(registroService.obtenerPorPlaca(placa)));
    }
}