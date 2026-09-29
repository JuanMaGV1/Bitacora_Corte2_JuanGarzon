package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.model.domain.*;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.validator.PedidoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository   pedidoRepository;
    private final MesaRepository     mesaRepository;
    private final PedidoEntityMapper entityMapper;
    private final PlatoService       platoService;
    private final PedidoValidator    validator;
    private final CuentaService      cuentaService;

    // Constructor manual con @Lazy para romper el ciclo Pedido ↔ Cuenta
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             MesaRepository mesaRepository,
                             PedidoEntityMapper entityMapper,
                             PlatoService platoService,
                             PedidoValidator validator,
                             @Lazy CuentaService cuentaService) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository   = mesaRepository;
        this.entityMapper     = entityMapper;
        this.platoService     = platoService;
        this.validator        = validator;
        this.cuentaService    = cuentaService;
    }

    // ─── LECTURA ────────────────────────────────────────────────────────

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAllWithItems().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Pedido> obtenerActivos() {
        return pedidoRepository.findByEstadoNotInWithItems(
                List.of(EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO)
        ).stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public List<Pedido> obtenerPorMesa(Long idMesa) {
        return pedidoRepository.findByMesaIdWithItems(idMesa).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findByIdWithItems(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", id));
    }

    // ─── SS-05: CONFIRMAR (con auto-agregar a cuenta) ───────────────────

    @Override
    @Transactional
    public Pedido confirmar(Long idMesa, List<Long> idPlatos, String notas) {
        // 1. La mesa debe existir
        MesaEntity mesaEntity = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new MesaNotFoundException("Mesa", idMesa));

        // 2. La mesa debe tener cuenta abierta
        if (!Boolean.TRUE.equals(mesaEntity.getCuentaAbierta())) {
            throw new EstadoInvalidoException(
                    "No se puede crear un pedido en la mesa " + mesaEntity.getNumero()
                    + " porque no tiene cuenta abierta");
        }

        // 3. Crear pedido + items
        PedidoEntity pedido = PedidoEntity.builder()
                .mesa(mesaEntity)
                .estado(EstadoPedido.RECIBIDO)
                .notas(notas)
                .build();

        for (Long idPlato : idPlatos) {
            Plato plato = platoService.obtenerPorId(idPlato);
            if (!plato.estaDisponible()) {
                throw new EstadoInvalidoException(
                        "El plato '" + plato.getNombre() + "' no está disponible");
            }
            ItemPedidoEntity item = ItemPedidoEntity.builder()
                    .idPlato(plato.getId())
                    .nombrePlato(plato.getNombre())
                    .precioCongelado(plato.getPrecio())
                    .cantidad(1)
                    .build();
            pedido.addItem(item);
        }

        PedidoEntity guardado = pedidoRepository.save(pedido);
        log.info("Pedido #{} creado para mesa {} — {} items",
                guardado.getId(), idMesa, guardado.getItems().size());

        // Agregar automáticamente a la cuenta abierta de la mesa
        try {
            Cuenta cuenta = cuentaService.obtenerPorMesa(idMesa);
            cuentaService.agregarPedido(cuenta.getId(), guardado.getId());
            log.info("Pedido #{} agregado automáticamente a cuenta #{}",
                    guardado.getId(), cuenta.getId());
        } catch (CuentaNotFoundException e) {
            // No debería pasar porque validamos cuentaAbierta arriba,
            // pero por si acaso lo logueamos sin romper el flujo
            log.warn("La mesa {} tiene cuentaAbierta=true pero no se encontró la cuenta",
                    idMesa);
        }

        return entityMapper.toDomain(guardado);
    }

    // ─── SS-02/03/04: MODIFICAR ────────────────────────────────────────

    @Override
    @Transactional
    public Pedido agregarItem(Long idPedido, Long idPlato, Integer cantidad) {
        PedidoEntity pedido = pedidoRepository.findByIdWithItems(idPedido)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", idPedido));

        validator.validarModificable(entityMapper.toDomain(pedido));

        Plato plato = platoService.obtenerPorId(idPlato);
        if (!plato.estaDisponible()) {
            throw new EstadoInvalidoException(
                    "El plato '" + plato.getNombre() + "' no está disponible");
        }

        boolean existe = pedido.getItems().stream()
                .anyMatch(i -> i.getIdPlato().equals(idPlato));

        if (existe) {
            pedido.getItems().stream()
                    .filter(i -> i.getIdPlato().equals(idPlato))
                    .findFirst()
                    .ifPresent(i -> i.setCantidad(i.getCantidad() + cantidad));
        } else {
            ItemPedidoEntity item = ItemPedidoEntity.builder()
                    .idPlato(plato.getId())
                    .nombrePlato(plato.getNombre())
                    .precioCongelado(plato.getPrecio())
                    .cantidad(cantidad)
                    .build();
            pedido.addItem(item);
        }

        log.info("Item agregado al pedido #{}: plato={} cantidad={}",
                idPedido, plato.getNombre(), cantidad);
        return entityMapper.toDomain(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public Pedido modificarItem(Long idPedido, Long idPlato, Integer nuevaCantidad) {
        PedidoEntity pedido = pedidoRepository.findByIdWithItems(idPedido)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", idPedido));

        validator.validarModificable(entityMapper.toDomain(pedido));

        ItemPedidoEntity item = pedido.getItems().stream()
                .filter(i -> i.getIdPlato().equals(idPlato))
                .findFirst()
                .orElseThrow(() -> new PedidoNotFoundException(
                        "Item con idPlato=" + idPlato, idPedido));

        if (nuevaCantidad <= 0) {
            pedido.getItems().remove(item);
            log.info("Item eliminado de pedido #{}: plato={}", idPedido, idPlato);
        } else {
            item.setCantidad(nuevaCantidad);
            log.info("Item modificado en pedido #{}: plato={} cantidad={}",
                    idPedido, idPlato, nuevaCantidad);
        }
        return entityMapper.toDomain(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public Pedido quitarItem(Long idPedido, Long idPlato) {
        PedidoEntity pedido = pedidoRepository.findByIdWithItems(idPedido)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", idPedido));

        validator.validarModificable(entityMapper.toDomain(pedido));

        boolean removed = pedido.getItems().removeIf(i -> i.getIdPlato().equals(idPlato));
        if (!removed) {
            throw new PedidoNotFoundException("Item con idPlato=" + idPlato, idPedido);
        }
        log.info("Item quitado de pedido #{}: plato={}", idPedido, idPlato);
        return entityMapper.toDomain(pedidoRepository.save(pedido));
    }

    // ─── SS-06: CAMBIAR ESTADO ─────────────────────────────────────────

    @Override
    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        PedidoEntity pedido = pedidoRepository.findByIdWithItems(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", id));

        validator.validarTransicion(entityMapper.toDomain(pedido), nuevoEstado);

        pedido.setEstado(nuevoEstado);
        log.info("Pedido #{} → {}", id, nuevoEstado);
        return entityMapper.toDomain(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        PedidoEntity pedido = pedidoRepository.findByIdWithItems(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido", id));

        Pedido dominio = entityMapper.toDomain(pedido);
        if (!dominio.getEstado().esCancelable()) {
            throw new EstadoInvalidoException(
                    "Un pedido en estado " + dominio.getEstado() + " no puede cancelarse");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
        log.info("Pedido #{} cancelado", id);
    }

    // ─── Verificar si un plato está en pedidos activos ─────────────────

    @Override
    public boolean tienePedidosActivosConPlato(Long idPlato) {
        return pedidoRepository.existsActivePedidoConPlato(idPlato);
    }
}