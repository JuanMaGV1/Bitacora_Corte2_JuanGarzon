package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.model.domain.*;
import com.restaurante.validator.PedidoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceImplTest {

    private PlatoService    platoService;
    private PedidoValidator validator;
    private PedidoServiceImpl service;

    @BeforeEach
    void setUp() {
        platoService = mock(PlatoService.class);
        validator    = mock(PedidoValidator.class);
        service      = new PedidoServiceImpl(platoService, validator);
    }

    private Plato plato(Long id, String nombre, double precio) {
        return Plato.builder()
                .id(id).nombre(nombre).precio(precio)
                .categoria("ROLL").disponible(true)
                .build();
    }

    @Test
    @DisplayName("confirmar — crea el pedido en estado RECIBIDO")
    void confirmar_pedidoValido_creaEnRecibido() {
        when(platoService.obtenerPorId(1L)).thenReturn(plato(1L, "Sashimi", 22000.0));

        Pedido pedido = service.confirmar(3L, List.of(1L, 1L), "sin wasabi");

        assertNotNull(pedido.getId());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        assertEquals(2, pedido.cantidadItems());
        assertEquals(44000.0, pedido.calcularTotal());
    }

    @Test
    @DisplayName("confirmar — roll agotado lanza EstadoInvalidoException")
    void confirmar_rollAgotado_lanzaExcepcion() {
        Plato agotado = plato(1L, "Sashimi", 22000.0);
        agotado.setDisponible(false);
        when(platoService.obtenerPorId(1L)).thenReturn(agotado);

        assertThrows(EstadoInvalidoException.class,
                () -> service.confirmar(3L, List.of(1L), null));
    }

    @Test
    @DisplayName("obtenerPorId — pedido no existe lanza PedidoNotFoundException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(PedidoNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("cambiarEstado — transición válida funciona")
    void cambiarEstado_valido_cambiaEstado() {
        when(platoService.obtenerPorId(1L)).thenReturn(plato(1L, "Sashimi", 22000.0));
        Pedido creado = service.confirmar(3L, List.of(1L), null);

        Pedido actualizado = service.cambiarEstado(creado.getId(), EstadoPedido.EN_PREPARACION);

        assertEquals(EstadoPedido.EN_PREPARACION, actualizado.getEstado());
    }

    @Test
    @DisplayName("cambiarEstado — transición inválida lanza EstadoInvalidoException")
    void cambiarEstado_invalido_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(plato(1L, "Sashimi", 22000.0));
        Pedido creado = service.confirmar(3L, List.of(1L), null);

        doThrow(new EstadoInvalidoException("no permitido"))
                .when(validator).validarTransicion(any(), any());

        assertThrows(EstadoInvalidoException.class,
                () -> service.cambiarEstado(creado.getId(), EstadoPedido.LISTO));
    }

    @Test
    @DisplayName("agregarItem — pedido no modificable lanza excepción")
    void agregarItem_noModificable_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(plato(1L, "Sashimi", 22000.0));
        Pedido creado = service.confirmar(3L, List.of(1L), null);

        doThrow(new PedidoNoModificableException("no modificable"))
                .when(validator).validarModificable(any());

        assertThrows(PedidoNoModificableException.class,
                () -> service.agregarItem(creado.getId(), 1L, 1));
    }

    @Test
    @DisplayName("obtenerActivos — filtra terminales")
    void obtenerActivos_filtraTerminales() {
        when(platoService.obtenerPorId(1L)).thenReturn(plato(1L, "Sashimi", 22000.0));
        Pedido p1 = service.confirmar(3L, List.of(1L), null);
        Pedido p2 = service.confirmar(3L, List.of(1L), null);
        service.cambiarEstado(p2.getId(), EstadoPedido.EN_PREPARACION);
        service.cambiarEstado(p2.getId(), EstadoPedido.LISTO);
        service.cambiarEstado(p2.getId(), EstadoPedido.ENTREGADO);

        List<Pedido> activos = service.obtenerActivos();

        assertEquals(1, activos.size());
    }

    @Test
    @DisplayName("obtenerTodos — lista vacía al inicio")
    void obtenerTodos_vacio() {
        assertTrue(service.obtenerTodos().isEmpty());
    }
}