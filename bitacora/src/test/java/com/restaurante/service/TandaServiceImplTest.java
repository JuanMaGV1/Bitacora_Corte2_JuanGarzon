package com.restaurante.service;

import com.restaurante.exception.NoEsRollException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.exception.TandaExcedidaException;
import com.restaurante.mapper.TandaDocumentMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.TandaResponseDTO;
import com.restaurante.persistence.document.TandaDocument;
import com.restaurante.repository.TandaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TandaServiceImplTest {

    private TandaRepository     tandaRepository;
    private TandaDocumentMapper documentMapper;
    private PlatoService        platoService;
    private TandaServiceImpl    service;

    @BeforeEach
    void setUp() {
        tandaRepository = mock(TandaRepository.class);
        documentMapper  = mock(TandaDocumentMapper.class);
        platoService    = mock(PlatoService.class);
        service         = new TandaServiceImpl(tandaRepository, documentMapper, platoService);
    }

    private Plato roll(Long id, String nombre, boolean disponible) {
        return Plato.builder()
                .id(id).nombre(nombre)
                .categoria("ROLL").disponible(disponible)
                .precio(18000.0).build();
    }

    private Plato noRoll(Long id, String nombre, String categoria) {
        return Plato.builder()
                .id(id).nombre(nombre)
                .categoria(categoria).disponible(true)
                .precio(5000.0).build();
    }

    @Test
    @DisplayName("✅ crear — tanda válida con 3 rolls")
    void crear_valida_ok() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "California", true));
        when(platoService.obtenerPorId(2L)).thenReturn(roll(2L, "Spicy Tuna", true));
        when(platoService.obtenerPorId(3L)).thenReturn(roll(3L, "Sashimi", true));

        TandaDocument guardado = TandaDocument.builder()
                .id("mongo-id-1").idsRolls(List.of(1L, 2L, 3L))
                .cantidad(3).estado("EN_PREPARACION")
                .fechaCreacion(LocalDateTime.now()).build();
        TandaResponseDTO response = TandaResponseDTO.builder()
        .id("mongo-id-1").cantidad(3).estado("EN_PREPARACION").build();

        when(tandaRepository.save(any())).thenReturn(guardado);
        when(documentMapper.toResponse(guardado)).thenReturn(response);

        TandaResponseDTO resultado = service.crear(List.of(1L, 2L, 3L));

        assertEquals(3, resultado.getCantidad());
        verify(tandaRepository).save(any(TandaDocument.class));
    }

    @Test
    @DisplayName("❌ crear — 7 rolls lanza TandaExcedidaException")
    void crear_sieteRolls_lanza() {
        assertThrows(TandaExcedidaException.class,
                () -> service.crear(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)));
    }

    @Test
    @DisplayName("❌ crear — lista vacía lanza IllegalArgumentException")
    void crear_vacia_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> service.crear(List.of()));
    }

    @Test
    @DisplayName("❌ crear — roll inexistente lanza PlatoNotFoundException")
    void crear_idInexistente_lanza() {
        when(platoService.obtenerPorId(999L))
                .thenThrow(new PlatoNotFoundException("Plato", 999L));

        assertThrows(PlatoNotFoundException.class,
                () -> service.crear(List.of(999L)));
    }

    @Test
    @DisplayName("❌ crear — plato no ROLL lanza NoEsRollException")
    void crear_noRoll_lanza() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "California", true));
        when(platoService.obtenerPorId(2L)).thenReturn(noRoll(2L, "Té verde", "BEBIDA"));

        assertThrows(NoEsRollException.class,
                () -> service.crear(List.of(1L, 2L)));
    }

    @Test
    @DisplayName("❌ crear — roll agotado lanza IllegalArgumentException")
    void crear_agotado_lanza() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "Sashimi", false));

        assertThrows(IllegalArgumentException.class,
                () -> service.crear(List.of(1L)));
    }

    @Test
    @DisplayName("✅ obtenerTodas — lista vacía al inicio")
    void obtenerTodas_vacio() {
        when(tandaRepository.findAll()).thenReturn(List.of());

        assertTrue(service.obtenerTodas().isEmpty());
    }
}