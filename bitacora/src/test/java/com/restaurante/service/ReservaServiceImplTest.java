package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.ReservaConflictoException;
import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.validator.ReservaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReservaServiceImplTest {

    private ReservaValidator validator;
    private MesaService      mesaService;
    private ReservaServiceImpl service;

    @BeforeEach
    void setUp() {
        validator   = new ReservaValidator();
        mesaService = mock(MesaService.class);
        service     = new ReservaServiceImpl(validator, mesaService);

        // Por defecto, todas las mesas existen con capacidad suficiente
        when(mesaService.obtenerPorId(anyLong())).thenReturn(
                Mesa.builder().id(3L).numero(3).capacidad(6).build());
    }

    private Reserva reserva() {
        return Reserva.builder()
                .idMesa(3L).cliente("Juan")
                .fechaHora(LocalDateTime.now().plusDays(1))
                .comensales(4)
                .build();
    }

    @Test
    @DisplayName("crear — reserva válida queda en PENDIENTE")
    void crear_valida_creaEnPendiente() {
        Reserva creada = service.crear(reserva());

        assertNotNull(creada.getId());
        assertEquals(EstadoReserva.PENDIENTE, creada.getEstado());
    }

    @Test
    @DisplayName("crear — conflicto de horario lanza excepción")
    void crear_conflicto_lanzaExcepcion() {
        service.crear(reserva());

        assertThrows(ReservaConflictoException.class,
                () -> service.crear(reserva()));
    }

    @Test
    @DisplayName("crear — mesa no existe lanza MesaNotFoundException")
    void crear_mesaNoExiste_lanzaExcepcion() {
        when(mesaService.obtenerPorId(999L))
                .thenThrow(new MesaNotFoundException("Mesa", 999L));

        Reserva r = reserva();
        r.setIdMesa(999L);

        assertThrows(MesaNotFoundException.class, () -> service.crear(r));
    }

    @Test
    @DisplayName("crear — comensales superan capacidad de la mesa")
    void crear_comensalesExcedenCapacidad_lanzaExcepcion() {
        Mesa mesaPequena = Mesa.builder().id(3L).numero(3).capacidad(2).build();
        when(mesaService.obtenerPorId(3L)).thenReturn(mesaPequena);

        Reserva r = reserva();
        r.setComensales(4);

        assertThrows(IllegalArgumentException.class, () -> service.crear(r));
    }

    @Test
    @DisplayName("crear — comensales igual a capacidad funciona")
    void crear_comensalesIgualCapacidad_ok() {
        Mesa mesaExacta = Mesa.builder().id(3L).numero(3).capacidad(4).build();
        when(mesaService.obtenerPorId(3L)).thenReturn(mesaExacta);

        Reserva r = reserva();
        r.setComensales(4);

        assertDoesNotThrow(() -> service.crear(r));
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza ReservaNotFoundException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(ReservaNotFoundException.class,
                () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("cambiarEstado — transición válida")
    void cambiarEstado_valido_cambia() {
        Reserva creada = service.crear(reserva());
        Reserva actualizada = service.cambiarEstado(creada.getId(), EstadoReserva.CONFIRMADA);

        assertEquals(EstadoReserva.CONFIRMADA, actualizada.getEstado());
    }

    @Test
    @DisplayName("cambiarEstado — transición inválida lanza excepción")
    void cambiarEstado_invalido_lanzaExcepcion() {
        Reserva creada = service.crear(reserva());
        service.cambiarEstado(creada.getId(), EstadoReserva.CONFIRMADA);
        service.cambiarEstado(creada.getId(), EstadoReserva.COMPLETADA);

        assertThrows(EstadoInvalidoException.class,
                () -> service.cambiarEstado(creada.getId(), EstadoReserva.CONFIRMADA));
    }

    @Test
    @DisplayName("obtenerVigentes — solo pendientes y confirmadas")
    void obtenerVigentes_filtraCorrecto() {
        Reserva r1 = service.crear(reserva());

        Reserva r2 = reserva();
        r2.setIdMesa(4L);
        when(mesaService.obtenerPorId(4L)).thenReturn(
                Mesa.builder().id(4L).numero(4).capacidad(6).build());

        Reserva creada2 = service.crear(r2);
        service.cancelar(creada2.getId());

        assertEquals(1, service.obtenerVigentes().size());
    }

    @Test
    @DisplayName("cancelar — reserva completada no se puede cancelar")
    void cancelar_completada_lanzaExcepcion() {
        Reserva creada = service.crear(reserva());
        service.cambiarEstado(creada.getId(), EstadoReserva.CONFIRMADA);
        service.cambiarEstado(creada.getId(), EstadoReserva.COMPLETADA);

        assertThrows(EstadoInvalidoException.class,
                () -> service.cancelar(creada.getId()));
    }

    @Test
    @DisplayName("obtenerTodas — lista vacía al inicio")
    void obtenerTodas_vacio() {
        assertTrue(service.obtenerTodas().isEmpty());
    }
}