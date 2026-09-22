package com.restaurante.service;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

import java.util.List;

public interface PedidoService {

    List<Pedido> obtenerTodos();
    List<Pedido> obtenerActivos();
    List<Pedido> obtenerPorMesa(Long idMesa);
    Pedido obtenerPorId(Long id);

    // SS-05
    Pedido confirmar(Long idMesa, List<Long> idPlatos, String notas);

    // SS-02, SS-03, SS-04
    Pedido agregarItem(Long idPedido, Long idPlato, Integer cantidad);
    Pedido modificarItem(Long idPedido, Long idPlato, Integer nuevaCantidad);
    Pedido quitarItem(Long idPedido, Long idPlato);

    // SS-06
    Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado);

    void cancelar(Long id);
}