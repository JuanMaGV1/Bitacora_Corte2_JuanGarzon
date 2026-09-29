package com.restaurante.service;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.validator.PedidoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceImplTest {

    private PedidoRepository   pedidoRepository;
    private MesaRepository     mesaRepository;
    private PedidoEntityMapper entityMapper;
    private PlatoService       platoService;
    private PedidoValidator    validator;
    private PedidoServiceImpl  service;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        mesaRepository   = mock(MesaRepository.class);
        entityMapper     = mock(PedidoEntityMapper.class);
        platoService     = mock(PlatoService.class);
        validator        = mock(PedidoValidator.class);
        service          = new PedidoServiceImpl(
                pedidoRepository, mesaRepository, entityMapper,
                platoService, validator);
    }

    private MesaEntity mesaAbierta() {
        return MesaEntity.builder().id(3L).numero(3).cuentaAbierta(true).build();
    }

    private Plato plato() {
        return Plato.builder().id(1L).nombre("Sashimi").precio(22000.0)
                .categoria("ROLL").disponible(true).build();
    }

    @Test
    @DisplayName("✅ confirmar — mesa con cuenta abierta")
    void confirmar_ok() {
        MesaEntity mesa = mesaAbierta();
        Plato plato = plato();
        PedidoEntity guardado = PedidoEntity.builder().id(1L).mesa(mesa).estado(EstadoPedido.RECIBIDO).build();
        Pedido dominio = Pedido.builder().id(1L).idMesa(3L).estado(EstadoPedido.RECIBIDO).build();

        when(mesaRepository.findById(3L)).thenReturn(Optional.of(mesa));
        when(platoService.obtenerPorId(1L)).thenReturn(plato);
        when(pedidoRepository.save(any())).thenReturn(guardado);
        when(entityMapper.toDomain(guardado)).thenReturn(dominio);

        Pedido resultado = service.confirmar(3L, List.of(1L), "sin wasabi");

        assertNotNull(resultado.getId());
        assertEquals(EstadoPedido.RECIBIDO, resultado.getEstado());
    }

    @Test
    @DisplayName("❌ confirmar — mesa sin cuenta abierta lanza excepción")
    void confirmar_mesaSinCuenta_lanza() {
        MesaEntity mesa = MesaEntity.builder().id(3L).numero(3).cuentaAbierta(false).build();
        when(mesaRepository.findById(3L)).thenReturn(Optional.of(mesa));

        assertThrows(EstadoInvalidoException.class,
                () -> service.confirmar(3L, List.of(1L), null));
    }

    @Test
    @DisplayName("❌ confirmar — mesa no existe lanza excepción")
    void confirmar_mesaNoExiste() {
        when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class,
                () -> service.confirmar(999L, List.of(1L), null));
    }

    @Test
    @DisplayName("❌ obtenerPorId — no existe lanza excepción")
    void obtenerPorId_noExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("✅ obtenerTodos — lista vacía")
    void obtenerTodos_vacio() {
        when(pedidoRepository.findAll()).thenReturn(List.of());

        assertTrue(service.obtenerTodos().isEmpty());
    }
}