package com.restaurante.service.impl;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlatoServiceImpl — pruebas unitarias")
class PlatoServiceImplTest {

    @Mock private PlatoRepository   platoRepository;
    @Mock private PlatoEntityMapper entityMapper;
    @Mock private PlatoValidator    validator;

    @InjectMocks private PlatoServiceImpl service;

    private PlatoEntity entity;
    private Plato       domain;

    @BeforeEach
    void setUp() {
        entity = PlatoEntity.builder()
                .id(1L).nombre("California Roll").precio(18000.0)
                .categoria("ROLL").descripcion("Cangrejo y aguacate")
                .disponible(true).build();

        domain = Plato.builder()
                .id(1L).nombre("California Roll").precio(18000.0)
                .categoria("ROLL").descripcion("Cangrejo y aguacate")
                .disponible(true).build();
    }

    // ── obtenerTodos ───────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodos — devuelve la lista mapeada")
    void obtenerTodos_devuelveListaMapeada() {
        when(platoRepository.findAll()).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        List<Plato> resultado = service.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("California Roll", resultado.get(0).getNombre());
        verify(platoRepository).findAll();
    }

    @Test
    @DisplayName("obtenerTodos — lista vacía si no hay platos")
    void obtenerTodos_listaVacia() {
        when(platoRepository.findAll()).thenReturn(List.of());

        List<Plato> resultado = service.obtenerTodos();

        assertTrue(resultado.isEmpty());
    }

    // ── obtenerDisponibles ─────────────────────────────────────────

    @Test
    @DisplayName("obtenerDisponibles — filtra solo los disponibles")
    void obtenerDisponibles_filtraSoloDisponibles() {
        when(platoRepository.findByDisponibleTrue()).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        List<Plato> resultado = service.obtenerDisponibles();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).estaDisponible());
        verify(platoRepository).findByDisponibleTrue();
    }

    // ── obtenerPorId ───────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId — encuentra el plato")
    void obtenerPorId_encontrado() {
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        Plato resultado = service.obtenerPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("California Roll", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId — lanza excepción si no existe")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(platoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(999L));
        verify(platoRepository).findById(999L);
    }

    // ── obtenerPorCategoria ────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorCategoria — devuelve platos de esa categoría")
    void obtenerPorCategoria_devuelveLista() {
        when(platoRepository.findByCategoriaIgnoreCase("ROLL")).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        List<Plato> resultado = service.obtenerPorCategoria("ROLL");

        assertEquals(1, resultado.size());
        verify(platoRepository).findByCategoriaIgnoreCase("ROLL");
    }

    // ── crear ──────────────────────────────────────────────────────

    @Test
    @DisplayName("crear — guarda y devuelve el plato creado")
    void crear_ok() {
        Plato nuevo = Plato.builder()
                .nombre("California Roll").precio(18000.0)
                .categoria("ROLL").descripcion("Cangrejo y aguacate").build();

        when(entityMapper.toEntity(nuevo)).thenReturn(entity);
        when(platoRepository.save(entity)).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        Plato resultado = service.crear(nuevo);

        assertNotNull(resultado.getId());
        assertTrue(resultado.estaDisponible());
        verify(validator).validarNombreUnico("California Roll");
        verify(validator).validarPrecioRazonable(18000.0);
        verify(platoRepository).save(entity);
    }

    @Test
    @DisplayName("crear — nombre duplicado lanza ConflictoException")
    void crear_nombreDuplicado_lanzaConflicto() {
        Plato nuevo = Plato.builder().nombre("California Roll").precio(18000.0).build();

        doThrow(new ConflictoException("Duplicado"))
                .when(validator).validarNombreUnico(anyString());

        assertThrows(ConflictoException.class, () -> service.crear(nuevo));
        verify(platoRepository, never()).save(any());
    }

    // ── actualizar ─────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar — modifica campos y guarda")
    void actualizar_ok() {
        Plato cambios = Plato.builder()
                .nombre("California Roll Premium").precio(22000.0)
                .categoria("ROLL").descripcion("Con salmón").build();

        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(platoRepository.save(any(PlatoEntity.class))).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        service.actualizar(1L, cambios);

        verify(validator).validarNombreUnicoExcluyendo("California Roll Premium", 1L);
        verify(platoRepository).save(any(PlatoEntity.class));
    }

    @Test
    @DisplayName("actualizar — id no existe lanza excepción")
    void actualizar_noExiste_lanzaExcepcion() {
        when(platoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.actualizar(999L, domain));
    }

    // ── cambiarDisponibilidad ──────────────────────────────────────

    @Test
    @DisplayName("cambiarDisponibilidad — desactiva un plato")
    void cambiarDisponibilidad_desactiva() {
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(platoRepository.save(entity)).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(domain);

        service.cambiarDisponibilidad(1L, false);

        assertFalse(entity.getDisponible());
        verify(platoRepository).save(entity);
    }

    // ── eliminar ───────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar — borra si existe")
    void eliminar_ok() {
        when(platoRepository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(platoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar — id no existe lanza excepción")
    void eliminar_noExiste_lanzaExcepcion() {
        when(platoRepository.existsById(999L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(999L));
        verify(platoRepository, never()).deleteById(anyLong());
    }
}