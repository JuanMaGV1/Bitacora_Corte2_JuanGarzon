package com.restaurante.controller;

import com.restaurante.mapper.CuentaMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(
                cuentaMapper.toResponseList(cuentaService.obtenerTodas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    public ResponseEntity<CuentaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.obtenerPorId(id)));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Obtener cuenta abierta de una mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
        @ApiResponse(responseCode = "404", description = "No hay cuenta abierta")
    })
    public ResponseEntity<CuentaResponseDTO> porMesa(@PathVariable Long idMesa) {
        return ResponseEntity.ok(
                cuentaMapper.toResponse(cuentaService.obtenerPorMesa(idMesa)));
    }

    @PostMapping("/mesa/{idMesa}")
    @Operation(summary = "Abrir cuenta para una mesa")
    @ApiResponse(responseCode = "201", description = "Cuenta abierta")
    public ResponseEntity<CuentaResponseDTO> abrir(@PathVariable Long idMesa) {
        Cuenta cuenta = cuentaService.abrir(idMesa);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cuentaMapper.toResponse(cuenta));
    }

    @PostMapping("/{id}/pedidos/{idPedido}")
    @Operation(summary = "Agregar un pedido a la cuenta")
    public ResponseEntity<CuentaResponseDTO> agregarPedido(
            @PathVariable Long id,
            @PathVariable Long idPedido) {
        Cuenta cuenta = cuentaService.agregarPedido(id, idPedido);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @PatchMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar cuenta y registrar pago")
    @ApiResponse(responseCode = "200", description = "Cuenta cerrada")
    public ResponseEntity<CuentaResponseDTO> cerrar(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.cerrar(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }
}