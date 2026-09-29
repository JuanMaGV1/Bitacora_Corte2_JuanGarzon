package com.restaurante.validator;

import com.restaurante.exception.IngredienteAgotadoException;
import com.restaurante.exception.IngredienteAlreadyExistsException;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngredienteValidator {

    private final IngredienteRepository ingredienteRepository;

    /** Regla 1: el nombre debe ser único */
    public void validarNombreUnico(String nombre) {
        if (ingredienteRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Ingrediente duplicado: '{}'", nombre);
            throw new IngredienteAlreadyExistsException(
                    "Ya existe un ingrediente con el nombre: " + nombre);
        }
    }

    /** Al actualizar, excluye el propio ingrediente */
    public void validarNombreUnicoExcluyendo(String nombre, Long id) {
        if (ingredienteRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new IngredienteAlreadyExistsException(
                    "Ya existe otro ingrediente con el nombre: " + nombre);
        }
    }

    /** Regla 2 (SS-RNF-06): ingredientes agotados bloquean rolls */
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