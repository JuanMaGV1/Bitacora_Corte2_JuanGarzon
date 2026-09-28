package com.restaurante.service;

import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.domain.Plato;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del Service con almacenamiento en memoria.
 * Cuando lleguemos a JPA (semana 9), solo cambiamos el Map por un Repository.
 */
@Slf4j
@Service
public class PlatoServiceImpl implements PlatoService {

    /** Almacén en memoria — reemplazable por Repository en el futuro */
    private final Map<Long, Plato> platos   = new ConcurrentHashMap<>();
    private final AtomicLong        contador = new AtomicLong(1);

    private final PlatoValidator validator;
    private final PedidoService  pedidoService;
    
    // Constructor manual con @Lazy para romper el ciclo Plato ↔ Pedido
    public PlatoServiceImpl(PlatoValidator validator,
                            @Lazy PedidoService pedidoService) {
        this.validator     = validator;
        this.pedidoService = pedidoService;
    }
    // ─── LECTURA ────────────────────────────────────────────────────────

    @Override
    public List<Plato> obtenerTodos() {
        log.debug("Obteniendo todos los platos. Total={}", platos.size());
        return platos.values().stream().toList();
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return platos.values().stream()
                .filter(Plato::estaDisponible)
                .toList();
    }

    @Override
    public List<Plato> obtenerPorCategoria(String categoria) {
        return platos.values().stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public Plato obtenerPorId(Long id) {
        return platos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Plato no encontrado: id={}", id);
                    return new PlatoNotFoundException("Plato", id);
                });
    }

    // ─── ESCRITURA ──────────────────────────────────────────────────────

    @Override
    public Plato crear(Plato plato) {
        validator.validarNombreUnico(plato.getNombre(), platos.values());
        validator.validarPrecioRazonable(plato.getPrecio());

        plato.setId(contador.getAndIncrement());
        plato.setDisponible(true);
        platos.put(plato.getId(), plato);

        log.info("Plato creado: id={}, nombre={}", plato.getId(), plato.getNombre());
        return plato;
    }

    @Override
    public Plato actualizar(Long id, Plato nuevosDatos) {
        Plato existente = obtenerPorId(id);

        // Validar nombre único excluyendo el propio plato que estamos actualizando
        List<Plato> otros = platos.values().stream()
                .filter(p -> !p.getId().equals(id))
                .toList();
        validator.validarNombreUnico(nuevosDatos.getNombre(), otros);
        validator.validarPrecioRazonable(nuevosDatos.getPrecio());

        existente.setNombre(nuevosDatos.getNombre());
        existente.setPrecio(nuevosDatos.getPrecio());
        existente.setCategoria(nuevosDatos.getCategoria());
        existente.setDescripcion(nuevosDatos.getDescripcion());

        log.info("Plato actualizado: id={}", id);
        return existente;
    }

    @Override
    public Plato cambiarDisponibilidad(Long id, boolean disponible) {
        Plato plato = obtenerPorId(id);
        if (disponible) plato.activar(); else plato.desactivar();
        log.info("Plato id={} → disponible={}", id, disponible);
        return plato;
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);

        if (pedidoService.tienePedidosActivosConPlato(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el plato: tiene pedidos activos");
        }

        platos.remove(id);
        log.info("Plato eliminado: id={}", id);
    }
}