package com.restaurante.service;

import com.restaurante.exception.CuentaNoAbiertaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CuentaServiceImplTest {

    private MesaService   mesaService;
    private PedidoService pedidoService;
    private CuentaServiceImpl service;

    @BeforeEach
    void setUp() {
        mesaService   = mock(MesaService.class);
        pedidoService = mock(PedidoService.class);
        service       = new CuentaServiceImpl(mesaService, pedidoService);
    }

    private Mesa mesa() {
        return Mesa.builder().id(1L).numero(1).capacidad(4).build();
    }

    @Test
    @DisplayName("abrir — crea cuenta ABIERTA con total 0")
    void abrir_creaAbierta() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesa());
        when(mesaService.abrirCuenta(1L)).thenReturn(mesa());

        Cuenta cuenta = service.abrir(1L);

        assertNotNull(cuenta.getId());
        assertTrue(cuenta.estaAbierta());
        assertEquals(0.0, cuenta.getTotal());
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza CuentaNotFoundException")
    void obtenerPorId_noExiste() {
        assertThrows(CuentaNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("obtenerPorMesa — sin cuenta abierta lanza excepción")
    void obtenerPorMesa_sinCuenta() {
        assertThrows(CuentaNotFoundException.class,
                () -> service.obtenerPorMesa(99L));
    }

    @Test
    @DisplayName("agregarPedido — suma el total")
    void agregarPedido_sumaTotal() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesa());
        when(mesaService.abrirCuenta(1L)).thenReturn(mesa());
        Cuenta cuenta = service.abrir(1L);

        Pedido pedido = Pedido.builder().id(10L)
                .items(List.of()).build();
        when(pedidoService.obtenerPorId(10L)).thenReturn(pedido);

        Cuenta actualizada = service.agregarPedido(cuenta.getId(), 10L);

        assertEquals(1, actualizada.getIdsPedidos().size());
    }

    @Test
    @DisplayName("cerrar — cambia estado a CERRADA")
    void cerrar_cambiaEstado() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesa());
        when(mesaService.abrirCuenta(1L)).thenReturn(mesa());
        when(mesaService.cerrarCuenta(1L)).thenReturn(mesa());

        Cuenta cuenta = service.abrir(1L);
        Cuenta cerrada = service.cerrar(cuenta.getId());

        assertFalse(cerrada.estaAbierta());
    }

    @Test
    @DisplayName("agregarPedido — cuenta cerrada lanza excepción")
    void agregarPedido_cerrada() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesa());
        when(mesaService.abrirCuenta(1L)).thenReturn(mesa());
        when(mesaService.cerrarCuenta(1L)).thenReturn(mesa());

        Cuenta cuenta = service.abrir(1L);
        service.cerrar(cuenta.getId());

        assertThrows(CuentaNoAbiertaException.class,
                () -> service.agregarPedido(cuenta.getId(), 1L));
    }

    @Test
    @DisplayName("obtenerTodas — lista vacía al inicio")
    void obtenerTodas_vacio() {
        assertTrue(service.obtenerTodas().isEmpty());
    }

    @Test
    @DisplayName("cerrar — con pedidos activos lanza excepción")
    void cerrar_conActivos_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesa());
        when(mesaService.abrirCuenta(1L)).thenReturn(mesa());
        when(mesaService.cerrarCuenta(1L)).thenReturn(mesa());

        Cuenta cuenta = service.abrir(1L);

        // Simular que tiene un pedido activo
        Pedido activo = Pedido.builder().id(10L).estado(EstadoPedido.RECIBIDO)
                .items(List.of()).build();
        when(pedidoService.obtenerPorId(10L)).thenReturn(activo);

        // Agregar el pedido a la cuenta (forzándolo con mock)
        when(pedidoService.obtenerPorId(anyLong()))
                .thenReturn(Pedido.builder().id(1L).items(List.of()).build());
        service.agregarPedido(cuenta.getId(), 10L);

        when(pedidoService.obtenerPorId(10L)).thenReturn(activo);

        assertThrows(CuentaNoAbiertaException.class,
                () -> service.cerrar(cuenta.getId()));
    }

}