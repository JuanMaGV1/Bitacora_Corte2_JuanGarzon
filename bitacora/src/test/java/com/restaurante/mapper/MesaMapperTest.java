package com.restaurante.mapper;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MesaMapperTest {

    private final MesaMapper mapper = new MesaMapperImpl();

    @Test
    @DisplayName("toDomain — crea mesa DISPONIBLE")
    void toDomain_creaDisponible() {
        MesaRequestDTO dto = MesaRequestDTO.builder().numero(1).capacidad(4).build();

        Mesa mesa = mapper.toDomain(dto);

        assertEquals(1, mesa.getNumero());
        assertEquals(EstadoMesa.DISPONIBLE, mesa.getEstado());
        assertFalse(mesa.tieneCuentaAbierta());
    }

    @Test
    @DisplayName("toResponse — convierte dominio a DTO")
    void toResponse_convierte() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build();

        MesaResponseDTO dto = mapper.toResponse(mesa);

        assertEquals(1L, dto.getId());
        assertEquals(EstadoMesa.OCUPADA, dto.getEstado());
    }

    @Test
    @DisplayName("toResponseList — convierte lista")
    void toResponseList_convierte() {
        assertEquals(2, mapper.toResponseList(List.of(
                Mesa.builder().id(1L).build(),
                Mesa.builder().id(2L).build())).size());
    }

    @Test
    @DisplayName("nulls devuelven null")
    void nulls_devuelvenNull() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toResponse(null));
        assertNull(mapper.toResponseList(null));
    }
}