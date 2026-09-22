package com.restaurante.mapper;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoMapperTest {

    private final PedidoMapper mapper = new PedidoMapperImpl();

    @Test
    @DisplayName("toItemResponse — calcula subtotal")
    void toItemResponse_calculaSubtotal() {
        ItemPedido item = ItemPedido.builder()
                .idPlato(1L).nombrePlato("Sashimi")
                .precioCongelado(22000.0).cantidad(2).build();

        ItemPedidoResponseDTO dto = mapper.toItemResponse(item);

        assertEquals(44000.0, dto.getSubtotal());
        assertEquals(2, dto.getCantidad());
    }

    @Test
    @DisplayName("toResponse — calcula total")
    void toResponse_calculaTotal() {
        Pedido pedido = Pedido.builder()
                .id(1L).idMesa(3L).estado(EstadoPedido.RECIBIDO)
                .timestamp(LocalDateTime.now())
                .items(List.of(
                        ItemPedido.builder().precioCongelado(10000.0).cantidad(2).build(),
                        ItemPedido.builder().precioCongelado(5000.0).cantidad(1).build()))
                .build();

        PedidoResponseDTO dto = mapper.toResponse(pedido);

        assertEquals(25000.0, dto.getTotal());
        assertEquals(EstadoPedido.RECIBIDO, dto.getEstado());
    }

    @Test
    @DisplayName("toResponseList — convierte lista")
    void toResponseList_convierte() {
        List<Pedido> pedidos = List.of(
                Pedido.builder().id(1L).items(List.of()).build(),
                Pedido.builder().id(2L).items(List.of()).build());

        assertEquals(2, mapper.toResponseList(pedidos).size());
    }

    @Test
    @DisplayName("toResponse(null) — devuelve null")
    void toResponse_null() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("toItemResponse(null) — devuelve null")
    void toItemResponse_null() {
        assertNull(mapper.toItemResponse(null));
    }

    @Test
    @DisplayName("toResponseList(null) — devuelve null")
    void toResponseList_null() {
        assertNull(mapper.toResponseList(null));
    }
}