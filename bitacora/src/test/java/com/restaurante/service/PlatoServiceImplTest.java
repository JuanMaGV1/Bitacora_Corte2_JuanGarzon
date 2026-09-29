package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlatoServiceImplTest {

    private PlatoRepository   platoRepository;
    private PlatoEntityMapper entityMapper;
    private PlatoValidator    validator;
    private PlatoServiceImpl  service;

    @BeforeEach
    void setUp() {
        platoRepository = mock(PlatoRepository.class);
        entityMapper    = mock(PlatoEntityMapper.class);
        validator       = mock(PlatoValidator.class);
        service         = new PlatoServiceImpl(platoRepository, entityMapper, validator);
    }

    private Plato plato(String nombre, double precio) {
        return Plato.builder()
                .nombre(nombre).precio(precio)
                .categoria("ROLL").descripcion("test").build();
    }

    private PlatoEntity entity(Long id, String nombre, double precio) {
        return PlatoEntity.builder()
                .id(id).nombre(nombre).precio(precio)
                .categoria("ROLL").disponible(true).build();
    }

    @Test
    @DisplayName("✅ crear — guarda el plato en BD")
    void crear_platoCorrecto_guarda() {
        Plato entrada = plato("California Roll", 18000.0);
        PlatoEntity entityGuardada = entity(1L, "California Roll", 18000.0);
        Plato dominio = Plato.builder().id(1L).nombre("California Roll").precio(18000.0)
                .categoria("ROLL").disponible(true).build();

        when(entityMapper.toEntity(entrada)).thenReturn(PlatoEntity.builder()
                .nombre("California Roll").precio(18000.0).categoria("ROLL").build());
        when(platoRepository.save(any(PlatoEntity.class))).thenReturn(entityGuardada);
        when(entityMapper.toDomain(entityGuardada)).thenReturn(dominio);

        Plato resultado = service.crear(entrada);

        assertNotNull(resultado.getId());
        assertEquals("California Roll", resultado.getNombre());
        verify(validator).validarNombreUnico("California Roll");
    }

    @Test
    @DisplayName("❌ crear — nombre duplicado lanza excepción")
    void crear_nombreDuplicado_lanza() {
        doThrow(new PlatoAlreadyExistsException("duplicado"))
                .when(validator).validarNombreUnico(any());

        assertThrows(PlatoAlreadyExistsException.class,
                () -> service.crear(plato("Tacos", 10000.0)));
    }

    @Test
    @DisplayName("❌ obtenerPorId — no existe lanza PlatoNotFoundException")
    void obtenerPorId_noExiste_lanza() {
        when(platoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PlatoNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    @Test
    @DisplayName("✅ obtenerPorId — existe devuelve dominio")
    void obtenerPorId_existe_devuelve() {
        PlatoEntity entity = entity(1L, "Sashimi", 25000.0);
        Plato dominio = Plato.builder().id(1L).nombre("Sashimi").precio(25000.0).build();

        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(dominio);

        Plato resultado = service.obtenerPorId(1L);

        assertEquals("Sashimi", resultado.getNombre());
    }

    @Test
    @DisplayName("✅ obtenerTodos — devuelve lista")
    void obtenerTodos_devuelveLista() {
        PlatoEntity e1 = entity(1L, "A", 1000.0);
        PlatoEntity e2 = entity(2L, "B", 2000.0);
        when(platoRepository.findAll()).thenReturn(List.of(e1, e2));
        when(entityMapper.toDomain(e1)).thenReturn(Plato.builder().id(1L).nombre("A").build());
        when(entityMapper.toDomain(e2)).thenReturn(Plato.builder().id(2L).nombre("B").build());

        List<Plato> resultado = service.obtenerTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("✅ obtenerTodos — lista vacía")
    void obtenerTodos_vacio() {
        when(platoRepository.findAll()).thenReturn(List.of());
        assertTrue(service.obtenerTodos().isEmpty());
    }

    @Test
    @DisplayName("✅ obtenerDisponibles — filtra por repository")
    void obtenerDisponibles_filtra() {
        PlatoEntity e1 = entity(1L, "A", 1000.0);
        when(platoRepository.findByDisponibleTrue()).thenReturn(List.of(e1));
        when(entityMapper.toDomain(e1)).thenReturn(Plato.builder().id(1L).nombre("A").build());

        assertEquals(1, service.obtenerDisponibles().size());
    }

    @Test
    @DisplayName("❌ eliminar — no existe lanza excepción")
    void eliminar_noExiste_lanza() {
        when(platoRepository.existsById(999L)).thenReturn(false);

        assertThrows(PlatoNotFoundException.class, () -> service.eliminar(999L));
    }

    @Test
    @DisplayName("✅ eliminar — existe borra del repository")
    void eliminar_existe_borra() {
        when(platoRepository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(platoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("✅ actualizar — modifica los campos")
    void actualizar_modifica() {
        PlatoEntity existente = entity(1L, "Original", 1000.0);
        Plato nuevosDatos = Plato.builder()
                .nombre("Modificado").precio(2000.0)
                .categoria("POSTRES").descripcion("nueva").build();

        when(platoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(platoRepository.save(existente)).thenReturn(existente);
        when(entityMapper.toDomain(existente)).thenReturn(Plato.builder()
                .id(1L).nombre("Modificado").precio(2000.0).categoria("POSTRES").build());

        Plato resultado = service.actualizar(1L, nuevosDatos);

        assertEquals("Modificado", resultado.getNombre());
        verify(validator).validarNombreUnicoExcluyendo("Modificado", 1L);
    }
}