package com.restaurante.service;

import com.restaurante.model.domain.*;
import com.restaurante.model.dto.response.IngresoCategoriaDTO;
import com.restaurante.model.dto.response.ResumenDiaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteServiceImplTest {

    private PedidoService pedidoService;
    private PlatoService  platoService;
    private CuentaService cuentaService;
    private ReporteServiceImpl service;

    @BeforeEach
    void setUp() {
        pedidoService = mock(PedidoService.class);
        platoService  = mock(PlatoService.class);
        cuentaService = mock(CuentaService.class);
        service       = new ReporteServiceImpl(pedidoService, platoService, cuentaService);
    }

    @Test
    @DisplayName("resumenDelDia — sin pedidos devuelve ceros")
    void resumen_sinPedidos() {
        when(pedidoService.obtenerTodos()).thenReturn(List.of());
        when(cuentaService.obtenerTodas()).thenReturn(List.of());

        ResumenDiaDTO r = service.resumenDelDia();

        assertEquals(0L, r.getTotalPedidos());
        assertEquals(0.0, r.getIngresoTotal());
        assertEquals("Sin pedidos", r.getPlatoMasPedido());
    }

    @Test
    @DisplayName("resumenDelDia — con pedidos calcula totales")
    void resumen_conPedidos() {
        Pedido pedido = Pedido.builder()
                .id(1L)
                .items(List.of(
                        ItemPedido.builder().nombrePlato("Sashimi")
                                .precioCongelado(22000.0).cantidad(2).build(),
                        ItemPedido.builder().nombrePlato("Roll")
                                .precioCongelado(18000.0).cantidad(1).build()))
                .build();
        when(pedidoService.obtenerTodos()).thenReturn(List.of(pedido));
        when(cuentaService.obtenerTodas()).thenReturn(List.of());

        ResumenDiaDTO r = service.resumenDelDia();

        assertEquals(1L, r.getTotalPedidos());
        assertEquals(62000.0, r.getIngresoTotal());
        assertEquals("Sashimi", r.getPlatoMasPedido());
    }

    @Test
    @DisplayName("ingresosPorCategoria — agrupa correctamente")
    void ingresosPorCategoria_agrupa() {
        when(platoService.obtenerTodos()).thenReturn(List.of(
                Plato.builder().categoria("ROLL").precio(18000.0).build(),
                Plato.builder().categoria("ROLL").precio(20000.0).build(),
                Plato.builder().categoria("BEBIDA").precio(5000.0).build()));

        List<IngresoCategoriaDTO> result = service.ingresosPorCategoria();

        assertEquals(2, result.size());
        IngresoCategoriaDTO roll = result.stream()
                .filter(i -> i.getCategoria().equals("ROLL"))
                .findFirst().orElseThrow();
        assertEquals(2L, roll.getCantidad());
        assertEquals(38000.0, roll.getIngreso());
    }

    @Test
    @DisplayName("platosPopulares — ordena y limita")
    void platosPopulares_top() {
        Pedido pedido = Pedido.builder()
                .items(List.of(
                        ItemPedido.builder().nombrePlato("A").precioCongelado(1000.0).cantidad(1).build(),
                        ItemPedido.builder().nombrePlato("A").precioCongelado(1000.0).cantidad(1).build(),
                        ItemPedido.builder().nombrePlato("B").precioCongelado(2000.0).cantidad(1).build()))
                .build();
        when(pedidoService.obtenerTodos()).thenReturn(List.of(pedido));

        List<String> top = service.platosPopulares(1);

        assertEquals(1, top.size());
        assertEquals("A", top.get(0));
    }
}