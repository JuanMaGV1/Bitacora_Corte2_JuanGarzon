package com.restaurante.service;

import com.restaurante.exception.CuentaNoAbiertaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository   cuentaRepository;
    private final CuentaEntityMapper entityMapper;
    private final MesaService        mesaService;
    private final PedidoService      pedidoService;

    @Override
    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta", id));
    }

    @Override
    public Cuenta obtenerPorMesa(Long idMesa) {
        return cuentaRepository.findByIdMesaAndEstadoIn(
                        idMesa, List.of(EstadoCuenta.ABIERTA, EstadoCuenta.EN_PAGO))
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new CuentaNotFoundException(
                        "No hay cuenta abierta para la mesa " + idMesa, null));
    }

    @Override
    @Transactional
    public Cuenta abrir(Long idMesa) {
        mesaService.abrirCuenta(idMesa);  // valida que no tenga cuenta abierta

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
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta", idCuenta));

        if (cuenta.getEstado() != EstadoCuenta.ABIERTA) {
            throw new CuentaNoAbiertaException(
                    "La cuenta " + idCuenta + " no está abierta");
        }

        Pedido pedido = pedidoService.obtenerPorId(idPedido);

        // Agrega el pedido a la lista
        cuenta.getIdsPedidos().add(idPedido);

        // SUMA EL TOTAL del pedido al total de la cuenta
        double totalPedido = pedido.calcularTotal();
        cuenta.setTotal(cuenta.getTotal() + totalPedido);

        CuentaEntity actualizada = cuentaRepository.save(cuenta);

        log.info("Pedido #{} agregado a cuenta #{}. Pedido=${}, Total cuenta=${}",
                idPedido, idCuenta, totalPedido, actualizada.getTotal());
        return entityMapper.toDomain(actualizada);
    }

    @Override
    @Transactional
    public Cuenta cerrar(Long id) {
        CuentaEntity cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta", id));

        if (cuenta.getEstado() == EstadoCuenta.CERRADA) {
            throw new CuentaNoAbiertaException("La cuenta ya está cerrada");
        }

        // Verificar que no haya pedidos activos
        boolean tieneActivos = cuenta.getIdsPedidos().stream()
                .map(pedidoService::obtenerPorId)
                .anyMatch(p -> p.getEstado() != EstadoPedido.ENTREGADO
                            && p.getEstado() != EstadoPedido.CANCELADO);

        if (tieneActivos) {
            throw new CuentaNoAbiertaException(
                    "No se puede cerrar la cuenta: aún hay pedidos activos en la mesa");
        }

        cuenta.setEstado(EstadoCuenta.CERRADA);
        CuentaEntity cerrada = cuentaRepository.save(cuenta);

        // Cerrar la mesa asociada
        mesaService.cerrarCuenta(cuenta.getIdMesa());

        log.info("Cuenta #{} cerrada. Total final=${}", id, cerrada.getTotal());
        return entityMapper.toDomain(cerrada);
    }
}