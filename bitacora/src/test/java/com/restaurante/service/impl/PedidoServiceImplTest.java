package com.restaurante.service.impl;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.model.domain.*;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.service.CuentaService;
import com.restaurante.service.PlatoService;
import com.restaurante.validator.PedidoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoServiceImpl — pruebas unitarias")
class PedidoServiceImplTest {

    @Mock private PedidoRepository   pedidoRepository;
    @Mock private MesaRepository     mesaRepository;
    @Mock private PedidoEntityMapper entityMapper;
    @Mock private PlatoService       platoService;
    @Mock private PedidoValidator    validator;
    @Mock private CuentaService      cuentaService;

    private PedidoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PedidoServiceImpl(
                pedidoRepository, mesaRepository, entityMapper,
                platoService, validator, cuentaService);
    }

    private MesaEntity mesaAbierta() {
        return MesaEntity.builder().id(1L).numero(1).cuentaAbierta(true).build();
    }

    private Plato platoDisponible() {
        return Plato.builder().id(1L).nombre("California Roll")
                .precio(18000.0).categoria("ROLL").disponible(true).build();
    }

    @Test
    @DisplayName("confirmar — mesa con cuenta abierta crea el pedido")
    void confirmar_ok() {
        MesaEntity mesa = mesaAbierta();
        Plato plato = platoDisponible();

        PedidoEntity guardado = PedidoEntity.builder()
                .id(1L).mesa(mesa).estado(EstadoPedido.RECIBIDO).build();
        Pedido dominio = Pedido.builder()
                .id(1L).idMesa(1L).estado(EstadoPedido.RECIBIDO).items(List.of()).build();

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(platoService.obtenerPorId(1L)).thenReturn(plato);
        when(pedidoRepository.save(any(PedidoEntity.class))).thenReturn(guardado);
        when(entityMapper.toDomain(guardado)).thenReturn(dominio);
        when(cuentaService.obtenerPorMesa(1L))
                .thenReturn(Cuenta.builder().id(10L).idMesa(1L).build());

        Pedido resultado = service.confirmar(1L, List.of(1L), "sin wasabi");

        assertNotNull(resultado);
        assertEquals(EstadoPedido.RECIBIDO, resultado.getEstado());
        verify(cuentaService).agregarPedido(10L, 1L);
    }

    @Test
    @DisplayName("confirmar — mesa sin cuenta abierta lanza excepción")
    void confirmar_sinCuenta_lanza() {
        MesaEntity mesa = MesaEntity.builder().id(1L).numero(1).cuentaAbierta(false).build();
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        assertThrows(EstadoInvalidoException.class,
                () -> service.confirmar(1L, List.of(1L), null));
    }

    @Test
    @DisplayName("confirmar — mesa inexistente lanza excepción")
    void confirmar_mesaNoExiste() {
        when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.confirmar(999L, List.of(1L), null));
    }

    @Test
    @DisplayName("confirmar — plato agotado lanza excepción")
    void confirmar_platoAgotado_lanza() {
        MesaEntity mesa = mesaAbierta();
        Plato agotado = Plato.builder().id(1L).nombre("Sashimi")
                .precio(22000.0).categoria("ROLL").disponible(false).build();

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(platoService.obtenerPorId(1L)).thenReturn(agotado);

        assertThrows(EstadoInvalidoException.class,
                () -> service.confirmar(1L, List.of(1L), null));
    }

    @Test
    @DisplayName("cambiarEstado — transición válida")
    void cambiarEstado_ok() {
        PedidoEntity entity = PedidoEntity.builder()
                .id(1L).estado(EstadoPedido.RECIBIDO).build();
        Pedido dominio = Pedido.builder()
                .id(1L).estado(EstadoPedido.RECIBIDO).items(List.of()).build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);
        when(pedidoRepository.save(entity)).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(dominio);

        service.cambiarEstado(1L, EstadoPedido.EN_PREPARACION);

        verify(validator).validarTransicion(any(), eq(EstadoPedido.EN_PREPARACION));
        assertEquals(EstadoPedido.EN_PREPARACION, entity.getEstado());
    }

    @Test
    @DisplayName("cancelar — RECIBIDO se cancela")
    void cancelar_ok() {
        PedidoEntity entity = PedidoEntity.builder()
                .id(1L).estado(EstadoPedido.RECIBIDO).build();
        Pedido dominio = Pedido.builder()
                .id(1L).estado(EstadoPedido.RECIBIDO).items(List.of()).build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);

        service.cancelar(1L);

        assertEquals(EstadoPedido.CANCELADO, entity.getEstado());
    }

    @Test
    @DisplayName("cancelar — LISTO no se puede cancelar")
    void cancelar_listo_lanza() {
        PedidoEntity entity = PedidoEntity.builder()
                .id(1L).estado(EstadoPedido.LISTO).build();
        Pedido dominio = Pedido.builder()
                .id(1L).estado(EstadoPedido.LISTO).items(List.of()).build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);

        assertThrows(EstadoInvalidoException.class, () -> service.cancelar(1L));
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza excepción")
    void obtenerPorId_noExiste() {
        when(pedidoRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(999L));
    }
}