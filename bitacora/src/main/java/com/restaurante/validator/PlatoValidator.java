package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatoValidator {

    private static final double PRECIO_MAXIMO = 1_000_000.0;
    public static final int MAX_ROLLS_POR_TANDA = 6;

    private final PlatoRepository platoRepository;

    /** Regla: el nombre del plato debe ser único en la carta. */
    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Intento de crear plato duplicado: '{}'", nombre);
            throw new PlatoAlreadyExistsException(
                    "Ya existe un plato con el nombre: " + nombre);
        }
    }

    /** Al actualizar, excluye el propio plato de la búsqueda. */
    public void validarNombreUnicoExcluyendo(String nombre, Long id) {
        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            log.warn("Intento de duplicar nombre al actualizar: '{}'", nombre);
            throw new PlatoAlreadyExistsException(
                    "Ya existe otro plato con el nombre: " + nombre);
        }
    }

    public void validarPrecioRazonable(Double precio) {
        if (precio != null && precio > PRECIO_MAXIMO) {
            throw new IllegalArgumentException(
                    "El precio no puede superar " + PRECIO_MAXIMO);
        }
    }

    public void validarTandaDeRolls(int cantidadRolls) {
        if (cantidadRolls > MAX_ROLLS_POR_TANDA) {
            throw new IllegalArgumentException(
                    "Una tanda no puede tener más de " + MAX_ROLLS_POR_TANDA + " rolls");
        }
    }
}