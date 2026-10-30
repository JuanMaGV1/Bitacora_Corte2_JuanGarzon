package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.Pedido;
import com.restaurante.core.model.domain.enums.EstadoPedido;

public interface PedidoService {
    List<Pedido> obtenerTodos();
    List<Pedido> obtenerActivos();
    List<Pedido> obtenerPorMesa(Long idMesa);
    Pedido obtenerPorId(Long id);
    Pedido confirmar(Long idMesa, List<Long> idPlatos, String notas);
    Pedido agregarItem(Long idPedido, Long idPlato, Integer cantidad);
    Pedido modificarItem(Long idPedido, Long idPlato, Integer nuevaCantidad);
    Pedido quitarItem(Long idPedido, Long idPlato);
    Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado);
    void cancelar(Long id);
}