package com.restaurante.controller;

import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.AgregarItemDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidoService.obtenerTodos()));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar pedidos activos")
    public ResponseEntity<List<PedidoResponseDTO>> activos() {
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidoService.obtenerActivos()));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Listar pedidos de una mesa")
    public ResponseEntity<List<PedidoResponseDTO>> porMesa(@PathVariable Long idMesa) {
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidoService.obtenerPorMesa(idMesa)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoMapper.toResponse(pedidoService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MESERO','GERENTE')")
    @Operation(summary = "Confirmar un nuevo pedido")
    public ResponseEntity<PedidoResponseDTO> confirmar(@RequestBody @Valid PedidoRequestDTO dto) {
        Pedido creado = pedidoService.confirmar(dto.getIdMesa(), dto.getIdPlatos(), dto.getNotas());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toResponse(creado));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Agregar un roll al pedido")
    public ResponseEntity<PedidoResponseDTO> agregarItem(
            @PathVariable Long id,
            @RequestBody @Valid AgregarItemDTO dto) {
        Pedido actualizado = pedidoService.agregarItem(id, dto.getIdPlato(), dto.getCantidad());
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @PutMapping("/{id}/items/{idPlato}")
    @Operation(summary = "Modificar cantidad de un roll")
    public ResponseEntity<PedidoResponseDTO> modificarItem(
            @PathVariable Long id,
            @PathVariable Long idPlato,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(pedidoMapper.toResponse(
                pedidoService.modificarItem(id, idPlato, cantidad)));
    }

    @DeleteMapping("/{id}/items/{idPlato}")
    @Operation(summary = "Quitar un roll del pedido")
    public ResponseEntity<PedidoResponseDTO> quitarItem(
            @PathVariable Long id,
            @PathVariable Long idPlato) {
        return ResponseEntity.ok(pedidoMapper.toResponse(
                pedidoService.quitarItem(id, idPlato)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('COCINERO','MESERO','GERENTE')")
    @Operation(summary = "Cambiar el estado del pedido")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado) {
        return ResponseEntity.ok(pedidoMapper.toResponse(
                pedidoService.cambiarEstado(id, nuevoEstado)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MESERO','GERENTE')")
    @Operation(summary = "Cancelar un pedido")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}