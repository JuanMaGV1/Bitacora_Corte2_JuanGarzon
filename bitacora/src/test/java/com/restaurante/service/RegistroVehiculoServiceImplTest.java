package com.restaurante.service;

import com.restaurante.exception.RegistroVehiculoNotFoundException;
import com.restaurante.mapper.RegistroVehiculoEntityMapper;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import com.restaurante.repository.RegistroVehiculoRepository;
import com.restaurante.validator.RegistroVehiculoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistroVehiculoServiceImplTest {

    private RegistroVehiculoRepository   registroRepository;
    private RegistroVehiculoEntityMapper entityMapper;
    private RegistroVehiculoValidator    validator;
    private RegistroVehiculoServiceImpl  service;

    @BeforeEach
    void setUp() {
        registroRepository = mock(RegistroVehiculoRepository.class);
        entityMapper       = mock(RegistroVehiculoEntityMapper.class);
        validator          = mock(RegistroVehiculoValidator.class);
        service            = new RegistroVehiculoServiceImpl(
                registroRepository, entityMapper, validator);
    }

    private RegistroVehiculoEntity entity(Long id, String placa, LocalDateTime salida) {
        return RegistroVehiculoEntity.builder()
                .id(id).placa(placa).entrada(LocalDateTime.now().minusHours(1))
                .salida(salida).cobro(salida != null ? 6000.0 : 0.0).build();
    }

    private RegistroVehiculo dominio(Long id, String placa, LocalDateTime salida) {
        return RegistroVehiculo.builder()
                .id(id).placa(placa).entrada(LocalDateTime.now().minusHours(1))
                .salida(salida).cobro(salida != null ? 6000.0 : 0.0).build();
    }

    @Test
    @DisplayName("registrarEntrada — crea registro con entrada")
    void registrarEntrada_ok() {
        RegistroVehiculo entrada = RegistroVehiculo.builder().placa("ABC-123").build();
        RegistroVehiculoEntity guardado = entity(1L, "ABC-123", null);
        RegistroVehiculo dominioGuardado = dominio(1L, "ABC-123", null);

        when(registroRepository.save(any())).thenReturn(guardado);
        when(entityMapper.toDomain(guardado)).thenReturn(dominioGuardado);

        RegistroVehiculo resultado = service.registrarEntrada(entrada);

        assertNotNull(resultado.getId());
        assertEquals("ABC-123", resultado.getPlaca());
        verify(validator).validarCapacidadDisponible();
        verify(validator).validarPlacaNoActiva("ABC-123");
    }

    @Test
    @DisplayName("registrarSalida — calcula cobro")
    void registrarSalida_calcula() {
        RegistroVehiculoEntity activo = entity(1L, "ABC-123", null);
        RegistroVehiculoEntity cerrado = entity(1L, "ABC-123", LocalDateTime.now());
        RegistroVehiculo dominioCerrado = dominio(1L, "ABC-123", LocalDateTime.now());

        when(registroRepository.findFirstByPlacaIgnoreCaseAndSalidaIsNull("ABC-123"))
                .thenReturn(Optional.of(activo));
        when(registroRepository.save(any())).thenReturn(cerrado);
        when(entityMapper.toDomain(cerrado)).thenReturn(dominioCerrado);

        RegistroVehiculo resultado = service.registrarSalida("ABC-123");

        assertNotNull(resultado.getSalida());
        assertTrue(resultado.getCobro() >= 0);
    }

    @Test
    @DisplayName("registrarSalida — sin registro activo lanza")
    void registrarSalida_sinActivo_lanza() {
        when(registroRepository.findFirstByPlacaIgnoreCaseAndSalidaIsNull("XXX-999"))
                .thenReturn(Optional.empty());

        assertThrows(RegistroVehiculoNotFoundException.class,
                () -> service.registrarSalida("XXX-999"));
    }

    @Test
    @DisplayName("cuposDisponibles — descuenta activos")
    void cuposDisponibles_descUenta() {
        when(registroRepository.countActivos()).thenReturn(5L);

        assertEquals(15, service.cuposDisponibles());
    }

    @Test
    @DisplayName("obtenerTodos — lista vacía")
    void obtenerTodos_vacio() {
        when(registroRepository.findAll()).thenReturn(List.of());

        assertTrue(service.obtenerTodos().isEmpty());
    }
}