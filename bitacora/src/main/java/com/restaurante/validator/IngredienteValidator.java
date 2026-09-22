package com.restaurante.validator;

import com.restaurante.exception.IngredienteAlreadyExistsException;
import com.restaurante.exception.IngredienteAgotadoException;
import com.restaurante.model.domain.Ingrediente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Validaciones de negocio del dominio Ingrediente.
 * Regla clave de Sakura Sushi: SS-RNF-06 — ingredientes agotados bloquean rolls.
 */
@Slf4j
@Component
public class IngredienteValidator {

    /** Regla 1: el nombre debe ser único */
    public void validarNombreUnico(String nombre, Collection<Ingrediente> existentes) {
        boolean duplicado = existentes.stream()
                .anyMatch(i -> i.getNombre().equalsIgnoreCase(nombre));

        if (duplicado) {
            log.warn("Intento de crear ingrediente duplicado: '{}'", nombre);
            throw new IngredienteAlreadyExistsException(
                    "Ya existe un ingrediente con el nombre: " + nombre);
        }
    }

    /**
     * Regla 2 (SS-RNF-06): al menos un ingrediente agotado bloquea la creación
     * de un roll personalizado.
     */
    public void validarDisponibles(List<Ingrediente> ingredientes) {
        List<String> agotados = ingredientes.stream()
                .filter(i -> !i.estaDisponible())
                .map(Ingrediente::getNombre)
                .toList();

        if (!agotados.isEmpty()) {
            log.warn("Roll bloqueado por ingredientes agotados: {}", agotados);
            throw new IngredienteAgotadoException(
                    "No se puede crear el roll: ingredientes agotados → " + agotados);
        }
    }
}