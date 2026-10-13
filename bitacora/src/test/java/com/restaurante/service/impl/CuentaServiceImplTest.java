package com.restaurante.service.impl;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.model.domain.*;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.service.MesaService;
import com.restaurante.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaServiceImpl — pruebas unitarias")
class CuentaServiceImplTest {

    @Mock private CuentaRepository   cuentaRepository;
    @Mock private CuentaEntityMapper entityMapper;
    @Mock private MesaService        mesaService;
    @Mock private PedidoService      pedidoService;

    private CuentaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CuentaServiceImpl(cuentaRepository, entityMapper, mesaService, pedidoService);
    }

    @Test
    @DisplayName("abrir — crea una cuenta en ABIERTA")
    void abrir_ok() {
        CuentaEntity guardada = CuentaEntity.builder()
                .id(1L).idMesa(1L).total(0.0).estado(EstadoCuenta.ABIERTA)
                .idsPedidos(new ArrayList<>()).build();
        Cuenta dominio = Cuenta.builder()
                .id(1L).idMesa(1L).total(0.0).estado(EstadoCuenta.ABIERTA).build();

        when(cuentaRepository.save(any())).thenReturn(guardada);
        when(entityMapper.toDomain(guardada)).thenReturn(dominio);

        Cuenta resultado = service.abrir(1L);

        assertTrue(resultado.estaAbierta());
        verify(mesaService).abrirCuenta(1L);
    }

    @Test
    @DisplayName("obtenerPorId — id inexistente lanza")
    void obtenerPorId_noExiste() {
        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("cerrar — cuenta cerrada lanza excepción")
    void cerrar_yaCerrada_lanza() {
        CuentaEntity entity = CuentaEntity.builder()
                .id(1L).idMesa(1L).estado(EstadoCuenta.CERRADA)
                .idsPedidos(new ArrayList<>()).total(0.0).build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(EstadoInvalidoException.class, () -> service.cerrar(1L));
    }

    @Test
    @DisplayName("cerrar — sin pedidos activos cierra correctamente")
    void cerrar_ok() {
        CuentaEntity entity = CuentaEntity.builder()
                .id(1L).idMesa(1L).estado(EstadoCuenta.ABIERTA)
                .idsPedidos(List.of(10L)).total(50000.0).build();
        CuentaEntity cerrada = CuentaEntity.builder()
                .id(1L).idMesa(1L).estado(EstadoCuenta.CERRADA)
                .idsPedidos(List.of(10L)).total(50000.0).build();
        Cuenta dominio = Cuenta.builder()
                .id(1L).idMesa(1L).estado(EstadoCuenta.CERRADA).build();

        Pedido entregado = Pedido.builder().id(10L)
                .estado(EstadoPedido.ENTREGADO).items(List.of()).build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(pedidoService.obtenerPorId(10L)).thenReturn(entregado);
        when(cuentaRepository.save(entity)).thenReturn(cerrada);
        when(entityMapper.toDomain(cerrada)).thenReturn(dominio);

        Cuenta resultado = service.cerrar(1L);

        assertFalse(resultado.estaAbierta());
        verify(mesaService).cerrarCuenta(1L);
    }

    @Test
    @DisplayName("cerrar — con pedidos activos lanza excepción")
    void cerrar_conActivos_lanza() {
        CuentaEntity entity = CuentaEntity.builder()
                .id(1L).idMesa(1L).estado(EstadoCuenta.ABIERTA)
                .idsPedidos(List.of(10L)).total(50000.0).build();

        Pedido activo = Pedido.builder().id(10L)
                .estado(EstadoPedido.RECIBIDO).items(List.of()).build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(pedidoService.obtenerPorId(10L)).thenReturn(activo);

        assertThrows(EstadoInvalidoException.class, () -> service.cerrar(1L));
    }
}