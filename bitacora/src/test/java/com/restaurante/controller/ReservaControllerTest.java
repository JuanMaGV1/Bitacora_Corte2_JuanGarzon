package com.restaurante.controller;

import com.restaurante.mapper.ReservaMapper;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.ReservaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired private MockMvc        mockMvc;
    @MockBean  private ReservaService reservaService;
    @MockBean  private ReservaMapper  reservaMapper;

    @Test
    @DisplayName("GET /reservas — 200")
    void listar_200() throws Exception {
        when(reservaService.obtenerTodas()).thenReturn(List.of());
        when(reservaMapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reservas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /reservas/999 — 404")
    void obtener_404() throws Exception {
        when(reservaService.obtenerPorId(999L))
                .thenThrow(new com.restaurante.exception.ReservaNotFoundException("Reserva", 999L));

        mockMvc.perform(get("/api/v1/reservas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /reservas/{id}/estado — 200")
    void cambiarEstado_200() throws Exception {
        Reserva r = Reserva.builder().id(1L).estado(EstadoReserva.CONFIRMADA).build();
        ReservaResponseDTO dto = ReservaResponseDTO.builder()
                .id(1L).estado(EstadoReserva.CONFIRMADA).build();

        when(reservaService.cambiarEstado(any(), any())).thenReturn(r);
        when(reservaMapper.toResponse(any())).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/reservas/1/estado")
                        .param("nuevoEstado", "CONFIRMADA"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /reservas/{id} — 204")
    void cancelar_204() throws Exception {
        mockMvc.perform(delete("/api/v1/reservas/1"))
                .andExpect(status().isNoContent());
    }
}