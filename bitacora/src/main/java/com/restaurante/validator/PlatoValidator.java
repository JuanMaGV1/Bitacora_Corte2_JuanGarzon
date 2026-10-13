package com.restaurante.validator;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatoValidator {

    private static final double PRECIO_MAXIMO = 1_000_000.0;

    private final PlatoRepository platoRepository;

    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Plato duplicado: '{}'", nombre);
            throw new ConflictoException("Ya existe un plato con el nombre: " + nombre);
        }
    }

    public void validarNombreUnicoExcluyendo(String nombre, Long id) {
        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe otro plato con el nombre: " + nombre);
        }
    }

    public void validarPrecioRazonable(Double precio) {
        if (precio != null && precio > PRECIO_MAXIMO) {
            throw new ReglaDeNegocioException("El precio no puede superar " + PRECIO_MAXIMO);
        }
    }
}