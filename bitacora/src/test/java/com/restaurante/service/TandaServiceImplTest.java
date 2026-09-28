package com.restaurante.service;

import com.restaurante.exception.NoEsRollException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.exception.TandaExcedidaException;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.TandaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TandaServiceImplTest {

    private PlatoService     platoService;
    private TandaServiceImpl service;

    @BeforeEach
    void setUp() {
        platoService = mock(PlatoService.class);
        service      = new TandaServiceImpl(platoService);
    }

    private Plato roll(Long id, String nombre) {
        return Plato.builder()
                .id(id).nombre(nombre)
                .categoria("ROLL").disponible(true)
                .precio(18000.0).build();
    }

    private Plato noRoll(Long id, String nombre, String categoria) {
        return Plato.builder()
                .id(id).nombre(nombre)
                .categoria(categoria).disponible(true)
                .precio(5000.0).build();
    }

    private Plato rollAgotado(Long id, String nombre) {
        return Plato.builder()
                .id(id).nombre(nombre)
                .categoria("ROLL").disponible(false)
                .precio(18000.0).build();
    }

    // ─── HAPPY PATH ──────────────────────────────────────────────

    @Test
    @DisplayName("crear — tanda válida con 3 rolls")
    void crear_valida_creaTanda() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "California Roll"));
        when(platoService.obtenerPorId(2L)).thenReturn(roll(2L, "Spicy Tuna"));
        when(platoService.obtenerPorId(3L)).thenReturn(roll(3L, "Sashimi Roll"));

        TandaResponseDTO tanda = service.crear(List.of(1L, 2L, 3L));

        assertNotNull(tanda.getId());
        assertEquals(3, tanda.getCantidad());
        assertEquals("EN_PREPARACION", tanda.getEstado());
        assertEquals(3, tanda.getIdsRolls().size());
    }

    @Test
    @DisplayName("crear — tanda con exactamente 6 rolls (límite)")
    void crear_seisRolls_ok() {
        for (long i = 1; i <= 6; i++) {
            when(platoService.obtenerPorId(i)).thenReturn(roll(i, "Roll " + i));
        }

        TandaResponseDTO tanda = service.crear(List.of(1L, 2L, 3L, 4L, 5L, 6L));

        assertEquals(6, tanda.getCantidad());
    }

    @Test
    @DisplayName("crear — tanda con 1 roll (mínimo)")
    void crear_unRoll_ok() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "Roll Único"));

        TandaResponseDTO tanda = service.crear(List.of(1L));

        assertEquals(1, tanda.getCantidad());
    }

    // ─── VALIDACIONES DE ENTRADA ─────────────────────────────────

    @Test
    @DisplayName("crear — lista vacía lanza IllegalArgumentException")
    void crear_listaVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> service.crear(List.of()));
    }

    @Test
    @DisplayName("crear — lista null lanza IllegalArgumentException")
    void crear_listaNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> service.crear(null));
    }

    // ─── REGLA DE SAKURA: MÁXIMO 6 ROLLS ────────────────────────

    @Test
    @DisplayName("crear — 7 rolls lanza TandaExcedidaException")
    void crear_sieteRolls_lanzaExcepcion() {
        assertThrows(TandaExcedidaException.class,
                () -> service.crear(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)));
    }

    @Test
    @DisplayName("crear — 10 rolls lanza TandaExcedidaException")
    void crear_diezRolls_lanzaExcepcion() {
        assertThrows(TandaExcedidaException.class,
                () -> service.crear(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)));
    }

    // ─── INTEGRIDAD REFERENCIAL ─────────────────────────────────

    @Test
    @DisplayName("crear — roll inexistente lanza PlatoNotFoundException")
    void crear_rollInexistente_lanzaExcepcion() {
        when(platoService.obtenerPorId(999L))
                .thenThrow(new PlatoNotFoundException("Plato", 999L));

        assertThrows(PlatoNotFoundException.class,
                () -> service.crear(List.of(999L)));
    }

    @Test
    @DisplayName("crear — uno de varios rolls no existe lanza excepción")
    void crear_unoDeVariosNoExiste_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "Roll 1"));
        when(platoService.obtenerPorId(2L)).thenReturn(roll(2L, "Roll 2"));
        when(platoService.obtenerPorId(999L))
                .thenThrow(new PlatoNotFoundException("Plato", 999L));

        assertThrows(PlatoNotFoundException.class,
                () -> service.crear(List.of(1L, 2L, 999L)));
    }

    // ─── REGLA DE SAKURA: SOLO ROLLS ────────────────────────────

    @Test
    @DisplayName("crear — plato no ROLL lanza NoEsRollException")
    void crear_noEsRoll_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "California Roll"));
        when(platoService.obtenerPorId(2L)).thenReturn(noRoll(2L, "Té verde", "BEBIDA"));

        assertThrows(NoEsRollException.class,
                () -> service.crear(List.of(1L, 2L)));
    }

    @Test
    @DisplayName("crear — todos no ROLL lanza NoEsRollException")
    void crear_todosNoRoll_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(noRoll(1L, "Té verde", "BEBIDA"));
        when(platoService.obtenerPorId(2L)).thenReturn(noRoll(2L, "Mochi", "POSTRE"));

        assertThrows(NoEsRollException.class,
                () -> service.crear(List.of(1L, 2L)));
    }

    // ─── REGLA: ROLLS DISPONIBLES ───────────────────────────────

    @Test
    @DisplayName("crear — roll agotado lanza IllegalArgumentException")
    void crear_rollAgotado_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "Sashimi Roll"));
        when(platoService.obtenerPorId(2L)).thenReturn(rollAgotado(2L, "Spicy Tuna"));

        assertThrows(IllegalArgumentException.class,
                () -> service.crear(List.of(1L, 2L)));
    }

    @Test
    @DisplayName("crear — todos agotados lanza IllegalArgumentException")
    void crear_todosAgotados_lanzaExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(rollAgotado(1L, "Roll 1"));
        when(platoService.obtenerPorId(2L)).thenReturn(rollAgotado(2L, "Roll 2"));

        assertThrows(IllegalArgumentException.class,
                () -> service.crear(List.of(1L, 2L)));
    }

    // ─── LECTURA ────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodas — lista vacía al inicio")
    void obtenerTodas_vacio() {
        assertTrue(service.obtenerTodas().isEmpty());
    }

    @Test
    @DisplayName("obtenerTodas — devuelve las tandas creadas")
    void obtenerTodas_devuelveTandas() {
        when(platoService.obtenerPorId(1L)).thenReturn(roll(1L, "Roll 1"));
        when(platoService.obtenerPorId(2L)).thenReturn(roll(2L, "Roll 2"));

        service.crear(List.of(1L));
        service.crear(List.of(2L));

        assertEquals(2, service.obtenerTodas().size());
    }

    // ─── ORDEN DE VALIDACIONES ──────────────────────────────────

    @Test
    @DisplayName("crear — máximo 6 se valida ANTES de consultar platos")
    void crear_maximo6SeValidaAntes() {
        // No mockeamos platoService — si intentara consultar, fallaría
        assertThrows(TandaExcedidaException.class,
                () -> service.crear(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)));
    }
}