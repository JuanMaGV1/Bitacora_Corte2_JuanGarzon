package com.restaurante.service;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

import java.util.List;

public interface PedidoService {
    List<Pedido> obtenerTodos();
    List<Pedido> obtenerActivos();
    Pedido obtenerPorId(Long id);
    Pedido confirmar(Long idMesa, List<Long> idPlatos, String notas);
    Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado);
    void cancelar(Long id);
}