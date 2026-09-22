package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired private MockMvc      mockMvc;
    @MockBean  private PedidoService pedidoService;
    @MockBean  private PedidoMapper  pedidoMapper;

    @Test
    @DisplayName("GET /pedidos — devuelve 200")
    void listar_200() throws Exception {
        when(pedidoService.obtenerTodos()).thenReturn(List.of());
        when(pedidoMapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /pedidos/999 — devuelve 404")
    void obtener_404() throws Exception {
        when(pedidoService.obtenerPorId(999L))
                .thenThrow(new com.restaurante.exception.PedidoNotFoundException("Pedido", 999L));

        mockMvc.perform(get("/api/v1/pedidos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /pedidos/1/estado — devuelve 200")
    void cambiarEstado_200() throws Exception {
        Pedido pedido = Pedido.builder().id(1L).estado(EstadoPedido.EN_PREPARACION).build();
        PedidoResponseDTO dto = PedidoResponseDTO.builder()
                .id(1L).estado(EstadoPedido.EN_PREPARACION).build();

        when(pedidoService.cambiarEstado(any(), any())).thenReturn(pedido);
        when(pedidoMapper.toResponse(any())).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/pedidos/1/estado")
                        .param("nuevoEstado", "EN_PREPARACION"))
                .andExpect(status().isOk());
    }
}