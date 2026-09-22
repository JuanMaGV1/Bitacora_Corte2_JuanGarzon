package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.mapper.IngredienteMapper;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.model.dto.request.IngredienteRequestDTO;
import com.restaurante.model.dto.response.IngredienteResponseDTO;
import com.restaurante.service.IngredienteService;
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

@WebMvcTest(IngredienteController.class)
class IngredienteControllerTest {

    @Autowired private MockMvc            mockMvc;
    @Autowired private ObjectMapper       json;
    @MockBean  private IngredienteService ingredienteService;
    @MockBean  private IngredienteMapper  ingredienteMapper;

    @Test
    @DisplayName("GET /ingredientes — 200")
    void listar_200() throws Exception {
        when(ingredienteService.obtenerTodos()).thenReturn(List.of());
        when(ingredienteMapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ingredientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /ingredientes — 201")
    void crear_201() throws Exception {
        IngredienteRequestDTO dto = IngredienteRequestDTO.builder()
                .nombre("Salmón").precio(3000.0).tipo("PESCADO").build();
        Ingrediente dominio = Ingrediente.builder().nombre("Salmón").precio(3000.0).build();
        Ingrediente creado  = Ingrediente.builder().id(1L).nombre("Salmón").disponible(true).build();
        IngredienteResponseDTO response = IngredienteResponseDTO.builder()
                .id(1L).nombre("Salmón").build();

        when(ingredienteMapper.toDomain(any())).thenReturn(dominio);
        when(ingredienteService.crear(any())).thenReturn(creado);
        when(ingredienteMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /ingredientes — 400 body inválido")
    void crear_400() throws Exception {
        mockMvc.perform(post("/api/v1/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /ingredientes/{id} — 204")
    void eliminar_204() throws Exception {
        mockMvc.perform(delete("/api/v1/ingredientes/1"))
                .andExpect(status().isNoContent());
    }
}