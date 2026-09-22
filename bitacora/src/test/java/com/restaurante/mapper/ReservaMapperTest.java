package com.restaurante.mapper;

import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaMapperTest {

    private final ReservaMapper mapper = new ReservaMapperImpl();

    @Test
    @DisplayName("toDomain — crea reserva PENDIENTE")
    void toDomain_creaPendiente() {
        ReservaRequestDTO dto = ReservaRequestDTO.builder()
                .idMesa(1L).cliente("Juan")
                .fechaHora(LocalDateTime.now().plusDays(1))
                .comensales(4).build();

        Reserva reserva = mapper.toDomain(dto);

        assertEquals("Juan", reserva.getCliente());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertNull(reserva.getId());
    }

    @Test
    @DisplayName("toResponse — convierte dominio a DTO")
    void toResponse_convierte() {
        Reserva r = Reserva.builder().id(1L).idMesa(3L).cliente("Juan")
                .fechaHora(LocalDateTime.now()).comensales(4)
                .estado(EstadoReserva.CONFIRMADA).build();

        ReservaResponseDTO dto = mapper.toResponse(r);

        assertEquals(1L, dto.getId());
        assertEquals(EstadoReserva.CONFIRMADA, dto.getEstado());
    }

    @Test
    @DisplayName("toResponseList — convierte lista")
    void toResponseList_convierte() {
        assertEquals(2, mapper.toResponseList(List.of(
                Reserva.builder().id(1L).build(),
                Reserva.builder().id(2L).build())).size());
    }

    @Test
    @DisplayName("nulls devuelven null")
    void nulls() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toResponse(null));
        assertNull(mapper.toResponseList(null));
    }
}