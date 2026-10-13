package com.restaurante.service.impl;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.IngredienteEntityMapper;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.persistence.entity.IngredienteEntity;
import com.restaurante.repository.IngredienteRepository;
import com.restaurante.validator.IngredienteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngredienteServiceImpl — pruebas unitarias")
class IngredienteServiceImplTest {

    @Mock private IngredienteRepository    ingredienteRepository;
    @Mock private IngredienteEntityMapper  entityMapper;
    @Mock private IngredienteValidator     validator;

    @InjectMocks private IngredienteServiceImpl service;

    private IngredienteEntity entity;
    private Ingrediente       domain;

    @BeforeEach
    void setUp() {
        entity = IngredienteEntity.builder().id(1L).nombre("Salmón").precio(3000.0)
                .tipo("PESCADO").disponible(true).build();
        domain = Ingrediente.builder().id(1L).nombre("Salmón").precio(3000.0)
                .tipo("PESCADO").disponible(true).build();
    }

    @Test
    @DisplayName("crear — guarda ingrediente")
    void crear_ok() {
        when(entityMapper.toEntity(any())).thenReturn(entity);
        when(ingredienteRepository.save(entity)).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        Ingrediente resultado = service.crear(
                Ingrediente.builder().nombre("Salmón").precio(3000.0).tipo("PESCADO").build());

        assertNotNull(resultado.getId());
        verify(validator).validarNombreUnico("Salmón");
    }

    @Test
    @DisplayName("crear — duplicado lanza ConflictoException")
    void crear_duplicado_lanza() {
        doThrow(new ConflictoException("dup")).when(validator).validarNombreUnico(anyString());

        assertThrows(ConflictoException.class,
                () -> service.crear(Ingrediente.builder().nombre("Salmón").build()));
    }

    @Test
    @DisplayName("obtenerTodos — lista mapeada")
    void obtenerTodos_ok() {
        when(ingredienteRepository.findAll()).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        assertEquals(1, service.obtenerTodos().size());
    }

    @Test
    @DisplayName("obtenerPorId — id inexistente lanza excepción")
    void obtenerPorId_noExiste() {
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("cambiarDisponibilidad — desactiva")
    void cambiarDisponibilidad_ok() {
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(ingredienteRepository.save(entity)).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        service.cambiarDisponibilidad(1L, false);

        assertFalse(entity.getDisponible());
    }

    @Test
    @DisplayName("eliminar — borra si existe")
    void eliminar_ok() {
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(ingredienteRepository).deleteById(1L);
    }
}