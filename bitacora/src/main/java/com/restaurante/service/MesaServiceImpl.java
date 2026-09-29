package com.restaurante.service;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.MesaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaRepository    mesaRepository;
    private final MesaEntityMapper  entityMapper;
    private final MesaValidator     validator;

    @Override
    public List<Mesa> obtenerTodas() {
        return mesaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Mesa> obtenerDisponibles() {
        return mesaRepository.findByEstado(EstadoMesa.DISPONIBLE).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Mesa obtenerPorId(Long id) {
        return mesaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new MesaNotFoundException("Mesa", id));
    }

    @Override
    public Mesa obtenerPorNumero(Integer numero) {
        return mesaRepository.findByNumero(numero)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new MesaNotFoundException("Mesa " + numero, null));
    }

    @Override
    public Mesa crear(Mesa mesa) {
        validator.validarNumeroUnico(mesa.getNumero());

        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setCuentaAbierta(false);

        MesaEntity guardada = mesaRepository.save(entityMapper.toEntity(mesa));
        log.info("Mesa creada: id={}, número={}", guardada.getId(), guardada.getNumero());
        return entityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Mesa abrirCuenta(Long id) {
        Mesa mesa = obtenerPorId(id);
        validator.validarAperturaCuenta(mesa);

        MesaEntity entity = mesaRepository.findById(id).orElseThrow();
        entity.setCuentaAbierta(true);
        entity.setEstado(EstadoMesa.OCUPADA);

        log.info("Cuenta abierta en mesa {}", entity.getNumero());
        return entityMapper.toDomain(mesaRepository.save(entity));
    }

    @Override
    @Transactional
    public Mesa cerrarCuenta(Long id) {
        Mesa mesa = obtenerPorId(id);
        validator.validarCierreCuenta(mesa);

        MesaEntity entity = mesaRepository.findById(id).orElseThrow();
        entity.setCuentaAbierta(false);
        entity.setEstado(EstadoMesa.DISPONIBLE);

        log.info("Cuenta cerrada en mesa {}", entity.getNumero());
        return entityMapper.toDomain(mesaRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        Mesa mesa = obtenerPorId(id);
        if (mesa.tieneCuentaAbierta()) {
            throw new MesaNoDisponibleException(
                    "No se puede eliminar la mesa " + mesa.getNumero() + ": tiene cuenta abierta");
        }
        mesaRepository.deleteById(id);
        log.info("Mesa eliminada: id={}", id);
    }
}