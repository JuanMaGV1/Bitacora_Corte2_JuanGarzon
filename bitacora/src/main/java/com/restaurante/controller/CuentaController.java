package com.restaurante.controller;

import com.restaurante.mapper.CuentaMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Gestión de cuentas y pagos")
public class CuentaController {

    private final CuentaService cuentaService;
    private final CuentaMapper  cuentaMapper;

    @GetMapping
    @Operation(summary = "Listar todas las cuentas")
    public ResponseEntity<List<CuentaResponseDTO>> listar() {
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentaService.obtenerTodas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    public ResponseEntity<CuentaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.obtenerPorId(id)));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Obtener cuenta abierta de una mesa")
    public ResponseEntity<CuentaResponseDTO> porMesa(@PathVariable Long idMesa) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.obtenerPorMesa(idMesa)));
    }

    @PostMapping("/mesa/{idMesa}")
    @PreAuthorize("hasAnyRole('MESERO','GERENTE')")
    @Operation(summary = "Abrir cuenta para una mesa")
    public ResponseEntity<CuentaResponseDTO> abrir(@PathVariable Long idMesa) {
        Cuenta cuenta = cuentaService.abrir(idMesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaMapper.toResponse(cuenta));
    }

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('MESERO','GERENTE')")
    @Operation(summary = "Cerrar cuenta y registrar pago")
    public ResponseEntity<CuentaResponseDTO> cerrar(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.cerrar(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }
}