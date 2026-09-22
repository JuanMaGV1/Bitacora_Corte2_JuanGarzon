package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.mapper.MesaMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MesaController.class)
class MesaControllerTest {

    @Autowired private MockMvc    mockMvc;
    @Autowired private ObjectMapper json;
    @MockBean  private MesaService mesaService;
    @MockBean  private MesaMapper  mesaMapper;

    @Test
    @DisplayName("GET /mesas — devuelve 200")
    void listar_200() throws Exception {
        when(mesaService.obtenerTodas()).thenReturn(List.of());
        when(mesaMapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/mesas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mesas — 201 Created")
    void crear_201() throws Exception {
        MesaRequestDTO dto = MesaRequestDTO.builder().numero(1).capacidad(4).build();
        Mesa domain = Mesa.builder().numero(1).capacidad(4).build();
        Mesa creada = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
        MesaResponseDTO response = MesaResponseDTO.builder()
                .id(1L).numero(1).estado(EstadoMesa.DISPONIBLE).build();

        when(mesaMapper.toDomain(any())).thenReturn(domain);
        when(mesaService.crear(any())).thenReturn(creada);
        when(mesaMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(1));
    }

    @Test
    @DisplayName("POST /mesas — body inválido 400")
    void crear_400() throws Exception {
        mockMvc.perform(post("/api/v1/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /mesas/{id}/abrir — 200")
    void abrir_200() throws Exception {
        Mesa abierta = Mesa.builder().id(1L).numero(1)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build();
        MesaResponseDTO response = MesaResponseDTO.builder()
                .id(1L).estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build();

        when(mesaService.abrirCuenta(1L)).thenReturn(abierta);
        when(mesaMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/mesas/1/abrir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuentaAbierta").value(true));
    }

    @Test
    @DisplayName("DELETE /mesas/{id} — 204")
    void eliminar_204() throws Exception {
        mockMvc.perform(delete("/api/v1/mesas/1"))
                .andExpect(status().isNoContent());
    }
}