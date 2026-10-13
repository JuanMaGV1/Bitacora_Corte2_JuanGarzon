package com.restaurante.controller;

import com.restaurante.mapper.MesaMapper;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
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
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Gestión de mesas del salón")
public class MesaController {

    private final MesaService mesaService;
    private final MesaMapper  mesaMapper;

    @GetMapping
    @Operation(summary = "Listar todas las mesas")
    public ResponseEntity<List<MesaResponseDTO>> listar() {
        return ResponseEntity.ok(mesaMapper.toResponseList(mesaService.obtenerTodas()));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar mesas disponibles")
    public ResponseEntity<List<MesaResponseDTO>> disponibles() {
        return ResponseEntity.ok(mesaMapper.toResponseList(mesaService.obtenerDisponibles()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una mesa por ID")
    public ResponseEntity<MesaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mesaMapper.toResponse(mesaService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Crear una nueva mesa")
    public ResponseEntity<MesaResponseDTO> crear(@RequestBody @Valid MesaRequestDTO dto) {
        Mesa creada = mesaService.crear(mesaMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaMapper.toResponse(creada));
    }

    @PatchMapping("/{id}/abrir")
    @PreAuthorize("hasAnyRole('GERENTE','MESERO')")
    @Operation(summary = "Abrir cuenta en una mesa")
    public ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(mesaMapper.toResponse(mesaService.abrirCuenta(id)));
    }

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('GERENTE','MESERO')")
    @Operation(summary = "Cerrar cuenta de una mesa")
    public ResponseEntity<MesaResponseDTO> cerrarCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(mesaMapper.toResponse(mesaService.cerrarCuenta(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Eliminar una mesa")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}