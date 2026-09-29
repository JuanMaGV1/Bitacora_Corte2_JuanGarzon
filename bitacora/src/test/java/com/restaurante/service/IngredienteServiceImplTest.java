package com.restaurante.service;

import com.restaurante.exception.IngredienteAlreadyExistsException;
import com.restaurante.exception.IngredienteNotFoundException;
import com.restaurante.mapper.IngredienteEntityMapper;
import com.restaurante.model.domain.Ingrediente;
import com.restaurante.persistence.entity.IngredienteEntity;
import com.restaurante.repository.IngredienteRepository;
import com.restaurante.validator.IngredienteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngredienteServiceImplTest {

    private IngredienteRepository    ingredienteRepository;
    private IngredienteEntityMapper  entityMapper;
    private IngredienteValidator     validator;
    private IngredienteServiceImpl   service;

    @BeforeEach
    void setUp() {
        ingredienteRepository = mock(IngredienteRepository.class);
        entityMapper          = mock(IngredienteEntityMapper.class);
        validator             = mock(IngredienteValidator.class);
        service               = new IngredienteServiceImpl(ingredienteRepository, entityMapper, validator);
    }

    private Ingrediente ingrediente(String nombre) {
        return Ingrediente.builder().nombre(nombre).precio(3000.0).tipo("PESCADO").build();
    }

    private IngredienteEntity entity(Long id, String nombre) {
        return IngredienteEntity.builder()
                .id(id).nombre(nombre).precio(3000.0)
                .tipo("PESCADO").disponible(true).build();
    }

    @Test
    @DisplayName("crear — guarda ingrediente")
    void crear_valido() {
        Ingrediente entrada = ingrediente("Salmón");
        IngredienteEntity guardada = entity(1L, "Salmón");
        Ingrediente dominio = Ingrediente.builder().id(1L).nombre("Salmón").disponible(true).build();

        when(entityMapper.toEntity(entrada)).thenReturn(IngredienteEntity.builder().nombre("Salmón").build());
        when(ingredienteRepository.save(any())).thenReturn(guardada);
        when(entityMapper.toDomain(guardada)).thenReturn(dominio);

        Ingrediente resultado = service.crear(entrada);

        assertNotNull(resultado.getId());
        verify(validator).validarNombreUnico("Salmón");
    }

    @Test
    @DisplayName("crear — duplicado lanza excepción")
    void crear_duplicado_lanza() {
        doThrow(new IngredienteAlreadyExistsException("dup"))
                .when(validator).validarNombreUnico(any());

        assertThrows(IngredienteAlreadyExistsException.class,
                () -> service.crear(ingrediente("Salmón")));
    }

    @Test
    @DisplayName("obtenerPorId — no existe lanza excepción")
    void obtenerPorId_noExiste() {
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IngredienteNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("obtenerTodos — lista vacía")
    void obtenerTodos_vacio() {
        when(ingredienteRepository.findAll()).thenReturn(List.of());

        assertTrue(service.obtenerTodos().isEmpty());
    }

    @Test
    @DisplayName("eliminar — borra del repository")
    void eliminar_existe() {
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(ingredienteRepository).deleteById(1L);
    }
}