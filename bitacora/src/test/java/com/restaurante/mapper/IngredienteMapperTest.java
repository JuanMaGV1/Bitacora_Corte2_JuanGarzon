package com.restaurante.mapper;

import com.restaurante.model.domain.Ingrediente;
import com.restaurante.model.dto.request.IngredienteRequestDTO;
import com.restaurante.model.dto.response.IngredienteResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngredienteMapperTest {

    private final IngredienteMapper mapper = new IngredienteMapperImpl();

    @Test
    @DisplayName("toDomain — crea ingrediente DISPONIBLE")
    void toDomain_creaDisponible() {
        IngredienteRequestDTO dto = IngredienteRequestDTO.builder()
                .nombre("Salmón").precio(3000.0).tipo("PESCADO").build();

        Ingrediente i = mapper.toDomain(dto);

        assertEquals("Salmón", i.getNombre());
        assertTrue(i.estaDisponible());
        assertNull(i.getId());
    }

    @Test
    @DisplayName("toResponse — convierte dominio a DTO")
    void toResponse_convierte() {
        Ingrediente i = Ingrediente.builder()
                .id(1L).nombre("Salmón").precio(3000.0)
                .tipo("PESCADO").disponible(true).build();

        IngredienteResponseDTO dto = mapper.toResponse(i);

        assertEquals(1L, dto.getId());
        assertEquals("PESCADO", dto.getTipo());
    }

    @Test
    @DisplayName("toResponseList + nulls")
    void toResponseList_yNulls() {
        assertEquals(1, mapper.toResponseList(List.of(
                Ingrediente.builder().id(1L).build())).size());
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toResponse(null));
        assertNull(mapper.toResponseList(null));
    }
}