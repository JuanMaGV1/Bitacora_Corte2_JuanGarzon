package com.restaurante.service.impl;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.service.CuentaService;
import com.restaurante.service.MesaService;
import com.restaurante.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository   cuentaRepository;
    private final CuentaEntityMapper entityMapper;
    private final MesaService        mesaService;
    private final PedidoService      pedidoService;

    public CuentaServiceImpl(CuentaRepository cuentaRepository,
                             CuentaEntityMapper entityMapper,
                             MesaService mesaService,
                             @Lazy PedidoService pedidoService) {
        this.cuentaRepository = cuentaRepository;
        this.entityMapper     = entityMapper;
        this.mesaService      = mesaService;
        this.pedidoService    = pedidoService;
    }

    @Override
    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.findAll().stream()
                .map(entityMapper::toDomain).toList();
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", id));
    }

    @Override
    public Cuenta obtenerPorMesa(Long idMesa) {
        return cuentaRepository.findByIdMesaAndEstadoIn(
                        idMesa, List.of(EstadoCuenta.ABIERTA, EstadoCuenta.EN_PAGO))
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay cuenta abierta para la mesa " + idMesa, idMesa));
    }

    @Override
    @Transactional
    public Cuenta abrir(Long idMesa) {
        mesaService.abrirCuenta(idMesa);

        CuentaEntity cuenta = CuentaEntity.builder()
                .idMesa(idMesa)
                .idsPedidos(new ArrayList<>())
                .total(0.0)
                .estado(EstadoCuenta.ABIERTA)
                .build();

        CuentaEntity guardada = cuentaRepository.save(cuenta);
        log.info("Cuenta abierta: id={}, mesa={}", guardada.getId(), idMesa);
        return entityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Cuenta agregarPedido(Long idCuenta, Long idPedido) {
        CuentaEntity cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", idCuenta));

        if (cuenta.getEstado() != EstadoCuenta.ABIERTA) {
            throw new EstadoInvalidoException("La cuenta " + idCuenta + " no está abierta");
        }

        Pedido pedido = pedidoService.obtenerPorId(idPedido);
        cuenta.getIdsPedidos().add(idPedido);
        cuenta.setTotal(cuenta.getTotal() + pedido.calcularTotal());

        CuentaEntity actualizada = cuentaRepository.save(cuenta);
        log.info("Pedido #{} agregado a cuenta #{}", idPedido, idCuenta);
        return entityMapper.toDomain(actualizada);
    }

    @Override
    @Transactional
    public Cuenta cerrar(Long id) {
        CuentaEntity cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", id));

        if (cuenta.getEstado() == EstadoCuenta.CERRADA) {
            throw new EstadoInvalidoException("La cuenta ya está cerrada");
        }

        boolean tieneActivos = cuenta.getIdsPedidos().stream()
                .map(pedidoService::obtenerPorId)
                .anyMatch(p -> p.getEstado() != EstadoPedido.ENTREGADO
                            && p.getEstado() != EstadoPedido.CANCELADO);

        if (tieneActivos) {
            throw new EstadoInvalidoException(
                "No se puede cerrar la cuenta: aún hay pedidos activos en la mesa");
        }

        cuenta.setEstado(EstadoCuenta.CERRADA);
        CuentaEntity cerrada = cuentaRepository.save(cuenta);
        mesaService.cerrarCuenta(cuenta.getIdMesa());

        log.info("Cuenta #{} cerrada. Total final=${}", id, cerrada.getTotal());
        return entityMapper.toDomain(cerrada);
    }
}