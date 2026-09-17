package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
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

/**
 * Pruebas del Controller con MockMvc.
 * Se mockean Service y Mapper — se prueba solo la capa web.
 */
@WebMvcTest(PlatoController.class)
class PlatoControllerTest {

    @Autowired private MockMvc        mockMvc;
    @Autowired private ObjectMapper   objectMapper;
    @MockBean  private PlatoService   platoService;
    @MockBean  private PlatoMapper    platoMapper;

    private Plato buildPlato(Long id, String nombre, double precio) {
        return Plato.builder()
                .id(id).nombre(nombre).precio(precio)
                .categoria("PRINCIPALES").descripcion("").disponible(true)
                .build();
    }

    private PlatoResponseDTO buildResponse(Long id, String nombre, double precio) {
        return PlatoResponseDTO.builder()
                .id(id).nombre(nombre).precio(precio)
                .categoria("PRINCIPALES").descripcion("").disponible(true)
                .build();
    }

    // ─── GET /api/v1/platos ─────────────────────────────────────────────

    @Test
    @DisplayName("✅ GET /platos — devuelve 200 con la lista")
    void listar_devuelve200ConLista() throws Exception {
        // GIVEN
        Plato plato = buildPlato(1L, "Bandeja Paisa", 28000.0);
        when(platoService.obtenerTodos()).thenReturn(List.of(plato));
        when(platoMapper.toResponseList(any()))
                .thenReturn(List.of(buildResponse(1L, "Bandeja Paisa", 28000.0)));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/platos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Bandeja Paisa"))
                .andExpect(jsonPath("$[0].precio").value(28000.0));
    }

    // ─── GET /api/v1/platos/{id} ────────────────────────────────────────

    @Test
    @DisplayName("✅ GET /platos/{id} — devuelve 200 con el plato")
    void obtenerPorId_devuelve200ConPlato() throws Exception {
        Plato plato = buildPlato(1L, "Bandeja Paisa", 28000.0);
        when(platoService.obtenerPorId(1L)).thenReturn(plato);
        when(platoMapper.toResponse(plato))
                .thenReturn(buildResponse(1L, "Bandeja Paisa", 28000.0));

        mockMvc.perform(get("/api/v1/platos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Bandeja Paisa"));
    }

    // ─── POST /api/v1/platos ────────────────────────────────────────────

    @Test
    @DisplayName("✅ POST /platos — body válido devuelve 201 Created")
    void crear_bodyValido_devuelve201() throws Exception {
        // GIVEN
        PlatoRequestDTO dto = new PlatoRequestDTO(
                "Bandeja Paisa", 28000.0, "PRINCIPALES", "Con chicharrón");

        Plato dominio = buildPlato(null, "Bandeja Paisa", 28000.0);
        Plato creado  = buildPlato(1L, "Bandeja Paisa", 28000.0);
        PlatoResponseDTO response = buildResponse(1L, "Bandeja Paisa", 28000.0);

        when(platoMapper.toDomain(any(PlatoRequestDTO.class))).thenReturn(dominio);
        when(platoService.crear(any(Plato.class))).thenReturn(creado);
        when(platoMapper.toResponse(any(Plato.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Bandeja Paisa"));
    }

    @Test
    @DisplayName("❌ POST /platos — body inválido devuelve 400 Bad Request")
    void crear_bodyInvalido_devuelve400() throws Exception {
        // Body sin nombre y con precio negativo
        String jsonInvalido = """
                {
                  "precio": -5,
                  "categoria": ""
                }
                """;

        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    // ─── DELETE /api/v1/platos/{id} ─────────────────────────────────────

    @Test
    @DisplayName("✅ DELETE /platos/{id} — devuelve 204 No Content")
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/platos/1"))
                .andExpect(status().isNoContent());
    }

    // ─── PATCH /api/v1/platos/{id}/disponible ───────────────────────────

    @Test
    @DisplayName("✅ PATCH /platos/{id}/disponible — devuelve 200 con el plato actualizado")
    void cambiarDisponibilidad_devuelve200() throws Exception {
        Plato actualizado = buildPlato(1L, "Bandeja Paisa", 28000.0);
        actualizado.setDisponible(false);

        PlatoResponseDTO response = buildResponse(1L, "Bandeja Paisa", 28000.0);
        response.setDisponible(false);

        when(platoService.cambiarDisponibilidad(1L, false)).thenReturn(actualizado);
        when(platoMapper.toResponse(actualizado)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/platos/1/disponible")
                        .param("disponible", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));
    }
}