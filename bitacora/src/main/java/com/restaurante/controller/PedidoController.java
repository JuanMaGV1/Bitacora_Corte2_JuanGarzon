package com.restaurante.controller;

import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Pedidos", description = "Flujo de pedidos y cocina")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper  pedidoMapper;

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

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoMapper.toResponse(pedidoService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Confirmar un nuevo pedido")
    public ResponseEntity<PedidoResponseDTO> crear(
            @RequestBody @Valid PedidoRequestDTO dto) {

        Pedido creado = pedidoService.confirmar(
                dto.getIdMesa(), dto.getIdPlatos(), dto.getNotas());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoMapper.toResponse(creado));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un pedido")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado) {

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