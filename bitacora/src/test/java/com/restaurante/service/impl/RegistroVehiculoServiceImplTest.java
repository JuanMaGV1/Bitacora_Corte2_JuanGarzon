package com.restaurante.service.impl;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.RegistroVehiculoEntityMapper;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import com.restaurante.repository.RegistroVehiculoRepository;
import com.restaurante.validator.RegistroVehiculoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistroVehiculoServiceImpl — pruebas unitarias")
class RegistroVehiculoServiceImplTest {

    @Mock private RegistroVehiculoRepository   registroRepository;
    @Mock private RegistroVehiculoEntityMapper entityMapper;
    @Mock private RegistroVehiculoValidator    validator;

    @InjectMocks private RegistroVehiculoServiceImpl service;

    @Test
    @DisplayName("registrarEntrada — asigna entrada y cobro 0")
    void registrarEntrada_ok() {
        RegistroVehiculo entrada = RegistroVehiculo.builder().placa("ABC-123").build();
        RegistroVehiculoEntity guardado = RegistroVehiculoEntity.builder()
                .id(1L).placa("ABC-123").entrada(LocalDateTime.now()).cobro(0.0).build();
        RegistroVehiculo dominio = RegistroVehiculo.builder()
                .id(1L).placa("ABC-123").entrada(LocalDateTime.now()).cobro(0.0).build();

        when(registroRepository.save(any())).thenReturn(guardado);
        when(entityMapper.toDomain(guardado)).thenReturn(dominio);

        RegistroVehiculo resultado = service.registrarEntrada(entrada);

        assertEquals(0.0, resultado.getCobro());
        verify(validator).validarCapacidadDisponible();
        verify(validator).validarPlacaNoActiva("ABC-123");
    }

    @Test
    @DisplayName("registrarSalida — calcula cobro")
    void registrarSalida_ok() {
        LocalDateTime entrada = LocalDateTime.now().minusMinutes(60);
        RegistroVehiculoEntity activo = RegistroVehiculoEntity.builder()
                .id(1L).placa("ABC-123").entrada(entrada).cobro(0.0).build();
        RegistroVehiculoEntity cerrado = RegistroVehiculoEntity.builder()
                .id(1L).placa("ABC-123").entrada(entrada)
                .salida(LocalDateTime.now()).cobro(6000.0).build();
        RegistroVehiculo dominio = RegistroVehiculo.builder()
                .id(1L).placa("ABC-123").cobro(6000.0).build();

        when(registroRepository.findFirstByPlacaIgnoreCaseAndSalidaIsNull("ABC-123"))
                .thenReturn(Optional.of(activo));
        when(registroRepository.save(activo)).thenReturn(cerrado);
        when(entityMapper.toDomain(cerrado)).thenReturn(dominio);

        RegistroVehiculo resultado = service.registrarSalida("ABC-123");

        assertNotNull(resultado.getCobro());
        assertTrue(resultado.getCobro() >= 0);
    }

    @Test
    @DisplayName("registrarSalida — sin registro activo lanza excepción")
    void registrarSalida_noActivo_lanza() {
        when(registroRepository.findFirstByPlacaIgnoreCaseAndSalidaIsNull("XXX-999"))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.registrarSalida("XXX-999"));
    }

    @Test
    @DisplayName("cuposDisponibles — descuenta los activos")
    void cuposDisponibles_ok() {
        when(registroRepository.countActivos()).thenReturn(5L);

        assertEquals(15, service.cuposDisponibles());
    }

    @Test
    @DisplayName("obtenerTodos — lista mapeada")
    void obtenerTodos_ok() {
        when(registroRepository.findAll()).thenReturn(List.of());

        assertTrue(service.obtenerTodos().isEmpty());
    }
}