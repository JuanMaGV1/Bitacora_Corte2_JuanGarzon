package com.restaurante.service;

import com.restaurante.exception.CuentaNoAbiertaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final Map<Long, Cuenta> cuentas = new ConcurrentHashMap<>();
    private final AtomicLong        contador = new AtomicLong(1);

    private final MesaService   mesaService;
    private final PedidoService pedidoService;

    @Override
    public List<Cuenta> obtenerTodas() {
        return cuentas.values().stream().toList();
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentas.values().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta", id));
    }

    @Override
    public Cuenta obtenerPorMesa(Long idMesa) {
        return cuentas.values().stream()
                .filter(c -> c.getIdMesa().equals(idMesa))
                .filter(Cuenta::estaAbierta)
                .findFirst()
                .orElseThrow(() -> new CuentaNotFoundException(
                        "No hay cuenta abierta para la mesa " + idMesa, null));
    }

    @Override
    public Cuenta abrir(Long idMesa) {
        Mesa mesa = mesaService.obtenerPorId(idMesa);
        mesaService.abrirCuenta(idMesa);

        Cuenta cuenta = Cuenta.builder()
                .id(contador.getAndIncrement())
                .idMesa(idMesa)
                .idsPedidos(new ArrayList<>())
                .total(0.0)
                .estado(EstadoCuenta.ABIERTA)
                .fechaApertura(LocalDateTime.now())
                .build();

        cuentas.put(cuenta.getId(), cuenta);
        log.info("Cuenta abierta: id={}, mesa={}", cuenta.getId(), mesa.getNumero());
        return cuenta;
    }

    @Override
    public Cuenta agregarPedido(Long idCuenta, Long idPedido) {
        Cuenta cuenta = obtenerPorId(idCuenta);
        if (!cuenta.estaAbierta()) {
            throw new CuentaNoAbiertaException(
                    "La cuenta " + idCuenta + " no está abierta");
        }

        Pedido pedido = pedidoService.obtenerPorId(idPedido);
        cuenta.agregarPedido(idPedido);
        cuenta.setTotal(cuenta.getTotal() + pedido.calcularTotal());

        log.info("Pedido #{} agregado a cuenta #{}. Total=${}",
                idPedido, idCuenta, cuenta.getTotal());
        return cuenta;
    }

    @Override
    public Cuenta cerrar(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        if (!cuenta.estaAbierta()) {
            throw new CuentaNoAbiertaException("La cuenta ya está cerrada");
        }

        // ─── Validación: no cerrar con pedidos activos ──────────────
        boolean tieneActivos = cuenta.getIdsPedidos().stream()
                .map(pedidoService::obtenerPorId)
                .anyMatch(p -> p.getEstado() != EstadoPedido.ENTREGADO
                            && p.getEstado() != EstadoPedido.CANCELADO);

        if (tieneActivos) {
            log.warn("Intento de cerrar cuenta #{} con pedidos activos", id);
            throw new CuentaNoAbiertaException(
                    "No se puede cerrar la cuenta: aún hay pedidos activos en la mesa");
        }

        cuenta.cerrarCuenta();
        mesaService.cerrarCuenta(cuenta.getIdMesa());

        log.info("Cuenta #{} cerrada. Total final=${}", id, cuenta.getTotal());
        return cuenta;
    }
}