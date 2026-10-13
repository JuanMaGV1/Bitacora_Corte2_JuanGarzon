package com.restaurante.controller;

import com.restaurante.mapper.RegistroVehiculoMapper;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.service.RegistroVehiculoService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/parqueadero")
@RequiredArgsConstructor
@Tag(name = "Parqueadero", description = "Registro de entrada y salida de vehículos")
public class ParqueaderoController {

    private final RegistroVehiculoService registroService;
    private final RegistroVehiculoMapper  registroMapper;

    @PostMapping("/entrada")
    @PreAuthorize("hasAnyRole('GERENTE','MESERO')")
    @Operation(summary = "Registrar entrada de un vehículo")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarEntrada(
            @RequestBody @Valid RegistroVehiculoRequestDTO dto) {
        RegistroVehiculo creado = registroService.registrarEntrada(
                registroMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registroMapper.toResponse(creado));
    }

    @PostMapping("/salida/{placa}")
    @PreAuthorize("hasAnyRole('GERENTE','MESERO')")
    @Operation(summary = "Registrar salida y calcular el cobro")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarSalida(@PathVariable String placa) {
        RegistroVehiculo cerrado = registroService.registrarSalida(placa);
        return ResponseEntity.ok(registroMapper.toResponse(cerrado));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar vehículos activos")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> activos() {
        return ResponseEntity.ok(registroMapper.toResponseList(registroService.obtenerActivos()));
    }

    @GetMapping("/estado")
    @Operation(summary = "Consultar cupos disponibles")
    public ResponseEntity<Map<String, Object>> estado() {
        return ResponseEntity.ok(Map.of(
                "cuposDisponibles", registroService.cuposDisponibles(),
                "vehiculosActivos", registroService.obtenerActivos().size()
        ));
    }

    @GetMapping("/placa/{placa}")
    @Operation(summary = "Historial de registros de una placa")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> porPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(registroMapper.toResponseList(
                registroService.obtenerPorPlaca(placa)));
    }
}