package com.restaurante.validator;

import com.restaurante.exception.IngredienteAgotadoException;
import com.restaurante.exception.IngredienteAlreadyExistsException;
import com.restaurante.model.domain.Ingrediente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngredienteValidatorTest {

    private final IngredienteValidator validator = new IngredienteValidator();

    private Ingrediente ingrediente(String nombre, boolean disponible) {
        return Ingrediente.builder().nombre(nombre).disponible(disponible).build();
    }

    @Test
    @DisplayName("validarNombreUnico — nombre nuevo no lanza")
    void nombreNuevo_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarNombreUnico("Aguacate", List.of(
                        ingrediente("Salmón", true))));
    }

    @Test
    @DisplayName("validarNombreUnico — duplicado lanza excepción")
    void nombreDuplicado_lanza() {
        assertThrows(IngredienteAlreadyExistsException.class, () ->
                validator.validarNombreUnico("Salmón", List.of(
                        ingrediente("Salmón", true))));
    }

    @Test
    @DisplayName("validarNombreUnico — case-insensitive")
    void nombreDuplicado_caseInsensitive() {
        assertThrows(IngredienteAlreadyExistsException.class, () ->
                validator.validarNombreUnico("SALMÓN", List.of(
                        ingrediente("Salmón", true))));
    }

    @Test
    @DisplayName("validarDisponibles — todos disponibles no lanza")
    void disponibles_todosOk() {
        assertDoesNotThrow(() ->
                validator.validarDisponibles(List.of(
                        ingrediente("Salmón", true),
                        ingrediente("Aguacate", true))));
    }

    @Test
    @DisplayName("validarDisponibles — uno agotado lanza IngredienteAgotadoException")
    void disponibles_unoAgotado_lanza() {
        assertThrows(IngredienteAgotadoException.class, () ->
                validator.validarDisponibles(List.of(
                        ingrediente("Salmón", true),
                        ingrediente("Atún", false))));
    }

    @Test
    @DisplayName("validarNombreUnico — lista vacía no lanza")
    void listaVacia_noLanza() {
        assertDoesNotThrow(() ->
                validator.validarNombreUnico("Cualquiera", List.of()));
    }
}