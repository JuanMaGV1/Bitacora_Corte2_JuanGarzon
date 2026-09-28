package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.model.domain.Plato;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Validaciones de negocio del dominio Plato.
 * Cada regla lanza una excepción tipada que el GlobalExceptionHandler captura.
 *
 * No es un @Service porque no tiene estado propio — es un @Component
 * que el Service inyecta para delegarle las validaciones.
 */
@Slf4j
@Component
public class PlatoValidator {

    /** Precio máximo razonable — regla de negocio del restaurante */
    private static final double PRECIO_MAXIMO = 1_000_000.0;

    /** Capacidad máxima de una tanda de rolls (regla de Sakura Sushi) */
    public static final int MAX_ROLLS_POR_TANDA = 6;

    /**
     * Regla de negocio de Sakura Sushi (SS-10):
     * Los rolls de la barra se preparan en tandas de máximo 6 unidades.
     */
    public void validarTandaDeRolls(int cantidadRolls) {
        if (cantidadRolls > MAX_ROLLS_POR_TANDA) {
            log.warn("Intento de crear tanda con {} rolls (máx {})",
                    cantidadRolls, MAX_ROLLS_POR_TANDA);
            throw new IllegalArgumentException(
                    "Una tanda de Sakura Sushi no puede tener más de "
                    + MAX_ROLLS_POR_TANDA + " rolls. Recibidos: " + cantidadRolls);
        }
    }
    /**
     * Regla 1: el nombre del plato debe ser único en la carta.
     * Comparación case-insensitive.
     *
     * @param nombre    nombre a validar
     * @param existentes platos actuales en la carta
     * @throws PlatoAlreadyExistsException si el nombre ya existe
     */
    public void validarNombreUnico(String nombre, Collection<Plato> existentes) {
        boolean duplicado = existentes.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));

        if (duplicado) {
            log.warn("Intento de crear plato duplicado: '{}'", nombre);
            throw new PlatoAlreadyExistsException(
                    "Ya existe un plato con el nombre: " + nombre);
        }
    }

    /**
     * Regla 2: el precio no puede superar un máximo razonable.
     * Evita errores de tipeo como "280000000" en vez de "28000".
     */
    public void validarPrecioRazonable(Double precio) {
        if (precio != null && precio > PRECIO_MAXIMO) {
            log.warn("Precio fuera de rango: {}", precio);
            throw new IllegalArgumentException(
                    "El precio no puede superar " + PRECIO_MAXIMO);
        }
    }
}