package com.restaurante.validator;

import com.restaurante.exception.ConflictoException;
import com.restaurante.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngredienteValidator {

    private final IngredienteRepository ingredienteRepository;

    public void validarNombreUnico(String nombre) {
        if (ingredienteRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un ingrediente con el nombre: " + nombre);
        }
    }

    public void validarNombreUnicoExcluyendo(String nombre, Long id) {
        if (ingredienteRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe otro ingrediente con el nombre: " + nombre);
        }
    }
}