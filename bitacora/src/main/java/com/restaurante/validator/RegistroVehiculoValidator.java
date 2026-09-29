package com.restaurante.validator;

import com.restaurante.exception.ParqueaderoLlenoException;
import com.restaurante.exception.RegistroVehiculoNotFoundException;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.repository.RegistroVehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistroVehiculoValidator {

    public static final int CAPACIDAD_MAXIMA = 20;

    private final RegistroVehiculoRepository registroRepository;

    /** Regla 1: no puede haber dos registros activos con la misma placa. */
    public void validarPlacaNoActiva(String placa) {
        if (registroRepository.existsByPlacaIgnoreCaseAndSalidaIsNull(placa)) {
            throw new ParqueaderoLlenoException(
                    "La placa " + placa + " ya tiene un registro activo");
        }
    }

    /** Regla 2: no se puede registrar entrada si el parqueadero está lleno. */
    public void validarCapacidadDisponible() {
        long activos = registroRepository.countActivos();
        if (activos >= CAPACIDAD_MAXIMA) {
            log.warn("Parqueadero lleno: {} activos de {}", activos, CAPACIDAD_MAXIMA);
            throw new ParqueaderoLlenoException(
                    "El parqueadero está lleno (" + CAPACIDAD_MAXIMA + " cupos)");
        }
    }

    /** Regla 3: la placa debe tener un registro activo para poder cerrarlo. */
    public RegistroVehiculo validarPlacaActiva(String placa) {
        return registroRepository
                .findFirstByPlacaIgnoreCaseAndSalidaIsNull(placa)
                .map(entity -> RegistroVehiculo.builder()
                        .id(entity.getId())
                        .placa(entity.getPlaca())
                        .entrada(entity.getEntrada())
                        .salida(entity.getSalida())
                        .cobro(entity.getCobro())
                        .build())
                .orElseThrow(() -> new RegistroVehiculoNotFoundException(
                        "No hay registro activo para la placa " + placa));
    }
}