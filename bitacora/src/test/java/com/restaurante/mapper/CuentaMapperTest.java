package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CuentaMapperTest {

    private final CuentaMapper mapper = new CuentaMapperImpl();

    @Test
    @DisplayName("toResponse — convierte dominio a DTO")
    void toResponse_convierte() {
        Cuenta c = Cuenta.builder()
                .id(1L).idMesa(3L)
                .idsPedidos(List.of(1L, 2L))
                .total(44000.0).estado(EstadoCuenta.ABIERTA)
                .fechaApertura(LocalDateTime.now()).build();

        CuentaResponseDTO dto = mapper.toResponse(c);

        assertEquals(1L, dto.getId());
        assertEquals(2, dto.getIdsPedidos().size());
        assertEquals(44000.0, dto.getTotal());
    }

    @Test
    @DisplayName("toResponseList + nulls")
    void toResponseList_yNulls() {
        assertEquals(1, mapper.toResponseList(List.of(
                Cuenta.builder().id(1L).idsPedidos(List.of()).build())).size());
        assertNull(mapper.toResponse(null));
        assertNull(mapper.toResponseList(null));
    }
}