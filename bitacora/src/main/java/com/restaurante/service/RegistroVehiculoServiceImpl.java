package com.restaurante.service;

import com.restaurante.exception.RegistroVehiculoNotFoundException;
import com.restaurante.mapper.RegistroVehiculoEntityMapper;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import com.restaurante.repository.RegistroVehiculoRepository;
import com.restaurante.validator.RegistroVehiculoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroVehiculoServiceImpl implements RegistroVehiculoService {

    private final RegistroVehiculoRepository   registroRepository;
    private final RegistroVehiculoEntityMapper entityMapper;
    private final RegistroVehiculoValidator    validator;

    @Override
    public List<RegistroVehiculo> obtenerTodos() {
        return registroRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<RegistroVehiculo> obtenerActivos() {
        return registroRepository.findActivos().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<RegistroVehiculo> obtenerPorPlaca(String placa) {
        return registroRepository.findByPlacaIgnoreCase(placa).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public RegistroVehiculo obtenerPorId(Long id) {
        return registroRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new RegistroVehiculoNotFoundException(
                        "No existe registro con id=" + id));
    }

    @Override
    @Transactional
    public RegistroVehiculo registrarEntrada(RegistroVehiculo registro) {
        validator.validarCapacidadDisponible();
        validator.validarPlacaNoActiva(registro.getPlaca());

        registro.setEntrada(LocalDateTime.now());
        registro.setSalida(null);
        registro.setCobro(0.0);

        RegistroVehiculoEntity guardado = registroRepository.save(entityMapper.toEntity(registro));

        log.info("Entrada registrada: placa={}, id={}",
                guardado.getPlaca(), guardado.getId());
        return entityMapper.toDomain(guardado);
    }

    @Override
    @Transactional
    public RegistroVehiculo registrarSalida(String placa) {
        RegistroVehiculoEntity entity = registroRepository
                .findFirstByPlacaIgnoreCaseAndSalidaIsNull(placa)
                .orElseThrow(() -> new RegistroVehiculoNotFoundException(
                        "No hay registro activo para la placa " + placa));

        LocalDateTime salida = LocalDateTime.now();
        entity.setSalida(salida);

        // Calcular cobro: minutos * 100
        long minutos = java.time.Duration.between(entity.getEntrada(), salida).toMinutes();
        double cobro = Math.round(minutos * 100.0 * 100.0) / 100.0;
        entity.setCobro(cobro);

        RegistroVehiculoEntity actualizado = registroRepository.save(entity);

        log.info("Salida registrada: placa={}, cobro={}", actualizado.getPlaca(), actualizado.getCobro());
        return entityMapper.toDomain(actualizado);
    }

    @Override
    public int cuposDisponibles() {
        long activos = registroRepository.countActivos();
        return (int) Math.max(0, RegistroVehiculoValidator.CAPACIDAD_MAXIMA - activos);
    }
}