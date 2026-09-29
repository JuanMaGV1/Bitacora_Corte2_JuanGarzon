package com.restaurante.service;

import com.restaurante.exception.CuentaNoAbiertaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.model.domain.*;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CuentaServiceImplTest {

    private CuentaRepository   cuentaRepository;
    private CuentaEntityMapper entityMapper;
    private MesaService        mesaService;
    private PedidoService      pedidoService;
    private CuentaServiceImpl  service;

    @BeforeEach
    void setUp() {
        cuentaRepository = mock(CuentaRepository.class);
        entityMapper     = mock(CuentaEntityMapper.class);
        mesaService      = mock(MesaService.class);
        pedidoService    = mock(PedidoService.class);
        service          = new CuentaServiceImpl(
                cuentaRepository, entityMapper, mesaService, pedidoService);
    }

    @Test
    @DisplayName("✅ abrir — crea cuenta ABIERTA")
    void abrir_ok() {
        CuentaEntity guardada = CuentaEntity.builder()
                .id(1L).idMesa(3L).total(0.0)
                .estado(EstadoCuenta.ABIERTA)
                .idsPedidos(new ArrayList<>()).build();
        Cuenta dominio = Cuenta.builder()
                .id(1L).idMesa(3L).total(0.0)
                .estado(EstadoCuenta.ABIERTA).build();

        when(cuentaRepository.save(any())).thenReturn(guardada);
        when(entityMapper.toDomain(guardada)).thenReturn(dominio);

        Cuenta resultado = service.abrir(3L);

        assertNotNull(resultado.getId());
        assertTrue(resultado.estaAbierta());
    }

    @Test
    @DisplayName("❌ obtenerPorId — no existe lanza excepción")
    void obtenerPorId_noExiste() {
        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("❌ cerrar — cuenta cerrada lanza excepción")
    void cerrar_yaCerrada_lanza() {
        CuentaEntity entity = CuentaEntity.builder()
                .id(1L).idMesa(3L).estado(EstadoCuenta.CERRADA)
                .idsPedidos(new ArrayList<>()).total(0.0).build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(CuentaNoAbiertaException.class, () -> service.cerrar(1L));
    }

    @Test
    @DisplayName("✅ cerrar — sin pedidos activos cierra")
    void cerrar_sinActivos_ok() {
        CuentaEntity entity = CuentaEntity.builder()
                .id(1L).idMesa(3L).estado(EstadoCuenta.ABIERTA)
                .idsPedidos(List.of(10L)).total(50000.0).build();
        CuentaEntity cerrada = CuentaEntity.builder()
                .id(1L).idMesa(3L).estado(EstadoCuenta.CERRADA)
                .idsPedidos(List.of(10L)).total(50000.0).build();
        Cuenta dominio = Cuenta.builder()
                .id(1L).idMesa(3L).estado(EstadoCuenta.CERRADA).build();

        Pedido pedidoEntregado = Pedido.builder().id(10L)
                .estado(EstadoPedido.ENTREGADO).items(List.of()).build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(pedidoService.obtenerPorId(10L)).thenReturn(pedidoEntregado);
        when(cuentaRepository.save(entity)).thenReturn(cerrada);
        when(entityMapper.toDomain(cerrada)).thenReturn(dominio);

        Cuenta resultado = service.cerrar(1L);

        assertFalse(resultado.estaAbierta());
    }
}