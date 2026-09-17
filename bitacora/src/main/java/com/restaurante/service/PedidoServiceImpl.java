package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Map<Long, Pedido> pedidos   = new ConcurrentHashMap<>();
    private final AtomicLong        contador  = new AtomicLong(1);

    private final PlatoService    platoService;   // reutiliza
    private final PedidoValidator validator;

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidos.values().stream().toList();
    }

    @Override
    public List<Pedido> obtenerActivos() {
        return pedidos.values().stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGADO
                          && p.getEstado() != EstadoPedido.CANCELADO)
                .toList();
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", id));
    }

    @Override
    public Pedido confirmar(Long idMesa, List<Long> idPlatos, String notas) {
        log.info("Confirmando pedido para mesa {}", idMesa);

        // Convertir idPlatos → ItemPedido, validando disponibilidad y congelando precio
        List<ItemPedido> items = idPlatos.stream()
                .map(idPlato -> {
                    Plato plato = platoService.obtenerPorId(idPlato);
                    if (!plato.estaDisponible()) {
                        throw new EstadoInvalidoException(
                                "El plato '" + plato.getNombre() + "' no está disponible");
                    }
                    return ItemPedido.builder()
                            .idPlato(plato.getId())
                            .nombrePlato(plato.getNombre())
                            .precioCongelado(plato.getPrecio())   // ← precio congelado
                            .cantidad(1)
                            .build();
                })
                .toList();

        Pedido pedido = Pedido.builder()
                .id(contador.getAndIncrement())
                .idMesa(idMesa)
                .items(items)
                .estado(EstadoPedido.RECIBIDO)
                .timestamp(LocalDateTime.now())
                .notas(notas)
                .build();

        pedidos.put(pedido.getId(), pedido);
        log.info("Pedido #{} creado para mesa {} — total={}",
                pedido.getId(), idMesa, pedido.calcularTotal());
        return pedido;
    }

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
        validator.validarModificable(pedido);

        pedido.setEstado(EstadoPedido.CANCELADO);
        log.info("Pedido #{} cancelado", id);
    }
}