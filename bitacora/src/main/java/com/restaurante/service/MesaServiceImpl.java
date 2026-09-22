package com.restaurante.service;

import com.restaurante.exception.MesaNoDisponibleException;   // ← AGREGAR
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.validator.MesaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final Map<Long, Mesa> mesas    = new ConcurrentHashMap<>();
    private final AtomicLong      contador = new AtomicLong(1);

    private final MesaValidator validator;

    @Override
    public List<Mesa> obtenerTodas() {
        return mesas.values().stream().toList();
    }

    @Override
    public List<Mesa> obtenerDisponibles() {
        return mesas.values().stream()
                .filter(Mesa::estaDisponible)
                .toList();
    }

    @Override
    public Mesa obtenerPorId(Long id) {
        return mesas.values().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new MesaNotFoundException("Mesa", id));
    }

    @Override
    public Mesa obtenerPorNumero(Integer numero) {
        return mesas.values().stream()
                .filter(m -> m.getNumero().equals(numero))
                .findFirst()
                .orElseThrow(() -> new MesaNotFoundException(
                        "Mesa con número " + numero, null));
    }

    @Override
    public Mesa crear(Mesa mesa) {
        validator.validarNumeroUnico(mesa.getNumero(), mesas.values());

        mesa.setId(contador.getAndIncrement());
        mesa.setEstado(EstadoMesa.DISPONIBLE);   // ← AGREGAR
        mesa.setCuentaAbierta(false);            // ← AGREGAR
        mesas.put(mesa.getId(), mesa);

        log.info("Mesa creada: id={}, número={}, capacidad={}",
                mesa.getId(), mesa.getNumero(), mesa.getCapacidad());
        return mesa;
    }

    @Override
    public Mesa abrirCuenta(Long id) {
        Mesa mesa = obtenerPorId(id);
        validator.validarAperturaCuenta(mesa);
        mesa.abrirCuenta();
        log.info("Cuenta abierta en mesa {}", mesa.getNumero());
        return mesa;
    }

    @Override
    public Mesa cerrarCuenta(Long id) {
        Mesa mesa = obtenerPorId(id);
        validator.validarCierreCuenta(mesa);
        mesa.cerrarCuenta();
        log.info("Cuenta cerrada en mesa {}", mesa.getNumero());
        return mesa;
    }

    @Override
    public void eliminar(Long id) {
        Mesa mesa = obtenerPorId(id);
        if (mesa.tieneCuentaAbierta()) {
            throw new MesaNoDisponibleException(
                    "No se puede eliminar la mesa " + mesa.getNumero()
                    + ": tiene cuenta abierta");
        }
        mesas.remove(id);
        log.info("Mesa eliminada: id={}", id);
    }
}