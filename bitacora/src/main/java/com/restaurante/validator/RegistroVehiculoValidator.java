package com.restaurante.validator;

import com.restaurante.exception.ConflictoException;
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

    public void validarPlacaNoActiva(String placa) {
        if (registroRepository.existsByPlacaIgnoreCaseAndSalidaIsNull(placa)) {
            throw new ConflictoException("La placa " + placa + " ya tiene un registro activo");
        }
    }

    public void validarCapacidadDisponible() {
        long activos = registroRepository.countActivos();
        if (activos >= CAPACIDAD_MAXIMA) {
            throw new ConflictoException(
                "El parqueadero está lleno (" + CAPACIDAD_MAXIMA + " cupos)");
        }
    }
}