package com.restaurante.controller;

import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.AgregarItemDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Flujo de pedidos y cocina (SS-02 a SS-06)")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper  pedidoMapper;

    // ─── LECTURA ────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(
                pedidoMapper.toResponseList(pedidoService.obtenerTodos()));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar pedidos activos (no entregados ni cancelados)")
    public ResponseEntity<List<PedidoResponseDTO>> activos() {
        return ResponseEntity.ok(
                pedidoMapper.toResponseList(pedidoService.obtenerActivos()));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Listar pedidos de una mesa")
    public ResponseEntity<List<PedidoResponseDTO>> porMesa(@PathVariable Long idMesa) {
        return ResponseEntity.ok(
                pedidoMapper.toResponseList(pedidoService.obtenerPorMesa(idMesa)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoMapper.toResponse(pedidoService.obtenerPorId(id)));
    }

    // ─── SS-05: CONFIRMAR ──────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Confirmar un nuevo pedido y enviarlo a cocina (SS-05)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido enviado a cocina"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "422", description = "Roll agotado o pedido vacío")
    })
    public ResponseEntity<PedidoResponseDTO> confirmar(
            @RequestBody @Valid PedidoRequestDTO dto) {

        log.info("POST /api/v1/pedidos — mesa={}", dto.getIdMesa());
        Pedido creado = pedidoService.confirmar(
                dto.getIdMesa(), dto.getIdPlatos(), dto.getNotas());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoMapper.toResponse(creado));
    }

    // ─── SS-02/03/04: MODIFICAR ────────────────────────────────────────

    @PostMapping("/{id}/items")
    @Operation(summary = "Agregar un roll al pedido (SS-02)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item agregado"),
        @ApiResponse(responseCode = "404", description = "Pedido o plato no existe"),
        @ApiResponse(responseCode = "422", description = "Pedido no modificable")
    })
    public ResponseEntity<PedidoResponseDTO> agregarItem(
            @PathVariable Long id,
            @RequestBody @Valid AgregarItemDTO dto) {

        Pedido actualizado = pedidoService.agregarItem(
                id, dto.getIdPlato(), dto.getCantidad());
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @PutMapping("/{id}/items/{idPlato}")
    @Operation(summary = "Modificar cantidad de un roll del pedido (SS-03)")
    public ResponseEntity<PedidoResponseDTO> modificarItem(
            @PathVariable Long id,
            @PathVariable Long idPlato,
            @RequestParam Integer cantidad) {

        Pedido actualizado = pedidoService.modificarItem(id, idPlato, cantidad);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}/items/{idPlato}")
    @Operation(summary = "Quitar un roll del pedido (SS-04)")
    public ResponseEntity<PedidoResponseDTO> quitarItem(
            @PathVariable Long id,
            @PathVariable Long idPlato) {

        Pedido actualizado = pedidoService.quitarItem(id, idPlato);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    // ─── SS-06: CAMBIAR ESTADO ─────────────────────────────────────────

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado del pedido desde cocina (SS-06)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Pedido no existe"),
        @ApiResponse(responseCode = "422", description = "Transición inválida")
    })
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado) {

        log.info("PATCH /api/v1/pedidos/{}/estado → {}", id, nuevoEstado);
        Pedido actualizado = pedidoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar un pedido (solo en RECIBIDO)")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}