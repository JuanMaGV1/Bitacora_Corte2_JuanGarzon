package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.model.domain.*;
import com.restaurante.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Map<Long, Pedido> pedidos  = new ConcurrentHashMap<>();
    private final AtomicLong        contador = new AtomicLong(1);

    private final PlatoService    platoService;   // reutiliza
    private final PedidoValidator validator;

    // ─── LECTURA ────────────────────────────────────────────────────────

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidos.values().stream().toList();
    }

    @Override
    public List<Pedido> obtenerActivos() {
        return pedidos.values().stream()
                .filter(p -> !p.getEstado().esTerminal())
                .toList();
    }

    @Override
    public List<Pedido> obtenerPorMesa(Long idMesa) {
        return pedidos.values().stream()
                .filter(p -> p.getIdMesa().equals(idMesa))
                .toList();
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", id));
    }

    // ─── SS-05: CONFIRMAR ──────────────────────────────────────────────

    @Override
    public Pedido confirmar(Long idMesa, List<Long> idPlatos, String notas) {
        log.info("Confirmando pedido para mesa {} con {} rolls", idMesa, idPlatos.size());

        List<ItemPedido> items = idPlatos.stream()
                .map(idPlato -> {
                    Plato plato = platoService.obtenerPorId(idPlato);
                    if (!plato.estaDisponible()) {
                        throw new EstadoInvalidoException(
                                "El roll '" + plato.getNombre() + "' no está disponible");
                    }
                    return ItemPedido.builder()
                            .idPlato(plato.getId())
                            .nombrePlato(plato.getNombre())
                            .precioCongelado(plato.getPrecio())
                            .cantidad(1)
                            .build();
                })
                .toList();

        Pedido pedido = Pedido.builder()
                .id(contador.getAndIncrement())
                .idMesa(idMesa)
                .items(new ArrayList<>(items))
                .estado(EstadoPedido.RECIBIDO)
                .timestamp(LocalDateTime.now())
                .notas(notas)
                .build();

        // Confirmar significa enviar a cocina — pasa a RECIBIDO
        validator.validarConfirmable(pedido);

        pedidos.put(pedido.getId(), pedido);
        log.info("Pedido #{} enviado a cocina — {} rolls, total=${}",
                pedido.getId(), pedido.cantidadItems(), pedido.calcularTotal());
        return pedido;
    }

    // ─── SS-02/03/04: MODIFICAR ────────────────────────────────────────

    @Override
    public Pedido agregarItem(Long idPedido, Long idPlato, Integer cantidad) {
        Pedido pedido = obtenerPorId(idPedido);
        validator.validarModificable(pedido);

        Plato plato = platoService.obtenerPorId(idPlato);
        if (!plato.estaDisponible()) {
            throw new EstadoInvalidoException(
                    "El roll '" + plato.getNombre() + "' no está disponible");
        }

        // Si ya existe, incrementar cantidad
        boolean existente = pedido.getItems().stream()
                .anyMatch(i -> i.getIdPlato().equals(idPlato));

        if (existente) {
            pedido.getItems().stream()
                    .filter(i -> i.getIdPlato().equals(idPlato))
                    .findFirst()
                    .ifPresent(i -> i.setCantidad(i.getCantidad() + cantidad));
        } else {
            pedido.getItems().add(ItemPedido.builder()
                    .idPlato(plato.getId())
                    .nombrePlato(plato.getNombre())
                    .precioCongelado(plato.getPrecio())
                    .cantidad(cantidad)
                    .build());
        }

        log.info("Item agregado al pedido #{}: plato={} cantidad={}",
                idPedido, plato.getNombre(), cantidad);
        return pedido;
    }

    @Override
    public Pedido modificarItem(Long idPedido, Long idPlato, Integer nuevaCantidad) {
        Pedido pedido = obtenerPorId(idPedido);
        validator.validarModificable(pedido);

        ItemPedido item = pedido.getItems().stream()
                .filter(i -> i.getIdPlato().equals(idPlato))
                .findFirst()
                .orElseThrow(() -> new PedidoNotFoundException(
                        "Item del pedido con idPlato=" + idPlato, idPedido));

        if (nuevaCantidad <= 0) {
            pedido.getItems().remove(item);
            log.info("Item eliminado del pedido #{} (cantidad 0): plato={}", idPedido, idPlato);
        } else {
            item.setCantidad(nuevaCantidad);
            log.info("Item modificado en pedido #{}: plato={} cantidad={}",
                    idPedido, idPlato, nuevaCantidad);
        }
        return pedido;
    }

    @Override
    public Pedido quitarItem(Long idPedido, Long idPlato) {
        Pedido pedido = obtenerPorId(idPedido);
        validator.validarModificable(pedido);

        boolean removed = pedido.getItems().removeIf(i -> i.getIdPlato().equals(idPlato));
        if (!removed) {
            throw new PedidoNotFoundException(
                    "Item del pedido con idPlato=" + idPlato, idPedido);
        }
        log.info("Item quitado del pedido #{}: plato={}", idPedido, idPlato);
        return pedido;
    }

    // ─── SS-06: CAMBIAR ESTADO ─────────────────────────────────────────

    @Override
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        validator.validarTransicion(pedido, nuevoEstado);

        pedido.setEstado(nuevoEstado);
        log.info("Pedido #{} → {}", id, nuevoEstado);
        return pedido;
    }

    @Override
    public void cancelar(Long id) {
        Pedido pedido = obtenerPorId(id);
        if (!pedido.getEstado().esCancelable()) {
            throw new EstadoInvalidoException(
                    "Un pedido en estado " + pedido.getEstado() + " no puede cancelarse");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        log.info("Pedido #{} cancelado", id);
    }
}