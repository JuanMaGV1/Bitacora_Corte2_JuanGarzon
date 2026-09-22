package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlatoMapperTest {

    private final PlatoMapper mapper = new PlatoMapperImpl();

    @Test
    @DisplayName("toDomain — convierte RequestDTO a dominio")
    void toDomain_convierte() {
        PlatoRequestDTO dto = PlatoRequestDTO.builder()
                .nombre("Sashimi").precio(22000.0)
                .categoria("ROLL").descripcion("fresco").build();

        Plato plato = mapper.toDomain(dto);

        assertEquals("Sashimi", plato.getNombre());
        assertEquals(22000.0, plato.getPrecio());
        assertTrue(plato.estaDisponible());
        assertNull(plato.getId());
    }

    @Test
    @DisplayName("toResponse — convierte dominio a DTO")
    void toResponse_convierte() {
        Plato plato = Plato.builder()
                .id(1L).nombre("Sashimi").precio(22000.0)
                .categoria("ROLL").disponible(true).build();

        PlatoResponseDTO dto = mapper.toResponse(plato);

        assertEquals(1L, dto.getId());
        assertEquals("Sashimi", dto.getNombre());
        assertTrue(dto.getDisponible());
    }

    @Test
    @DisplayName("toResponseList — convierte lista")
    void toResponseList_convierte() {
        List<Plato> platos = List.of(
                Plato.builder().id(1L).nombre("A").build(),
                Plato.builder().id(2L).nombre("B").build());

        assertEquals(2, mapper.toResponseList(platos).size());
    }

    @Test
    @DisplayName("toDomain(null) — devuelve null")
    void toDomain_null_devuelveNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("toResponse(null) — devuelve null")
    void toResponse_null_devuelveNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("toResponseList(null) — devuelve null")
    void toResponseList_null_devuelveNull() {
        assertNull(mapper.toResponseList(null));
    }
}