package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.domain.Plato;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PlatoServiceImpl.
 * Se mockea el Validator para controlar las validaciones de negocio.
 */
class PlatoServiceImplTest {

    private PlatoValidator     validator;   // doble controlado
    private PlatoServiceImpl   service;     // clase real bajo prueba
    private PedidoService pedidoService;

     @BeforeEach
    void setUp() {
        validator     = mock(PlatoValidator.class);
        pedidoService = mock(PedidoService.class);   // ← AGREGAR
        service       = new PlatoServiceImpl(validator, pedidoService);
    }

    /** Helper para crear un Plato de prueba */
    private Plato plato(String nombre, double precio) {
        return Plato.builder()
                .nombre(nombre)
                .precio(precio)
                .categoria("PRINCIPALES")
                .descripcion("Descripción de prueba")
                .build();
    }

    // ─── HAPPY PATH ─────────────────────────────────────────────────────

    @Test
    @DisplayName("✅ crear — guarda el plato y le asigna un ID")
    void crear_platoCorrecto_guardaYRetorna() {
        // GIVEN
        Plato entrada = plato("Bandeja Paisa", 28000.0);

        // WHEN
        Plato resultado = service.crear(entrada);

        // THEN
        assertNotNull(resultado.getId(), "El ID debe ser asignado");
        assertEquals("Bandeja Paisa", resultado.getNombre());
        assertEquals(28000.0, resultado.getPrecio());
        assertTrue(resultado.estaDisponible(), "Todo plato nuevo empieza disponible");

        // Verifica que el Validator fue llamado exactamente 1 vez
        verify(validator, times(1)).validarNombreUnico(any(), any());
        verify(validator, times(1)).validarPrecioRazonable(any());
    }

    @Test
    @DisplayName("✅ obtenerTodos — devuelve todos los platos")
    void obtenerTodos_devuelveTodosLosPlatos() {
        service.crear(plato("A", 1000.0));
        service.crear(plato("B", 2000.0));

        List<Plato> resultado = service.obtenerTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("✅ obtenerDisponibles — filtra solo los disponibles")
    void obtenerDisponibles_filtraSoloDisponibles() {
        Plato a = service.crear(plato("A", 1000.0));
        Plato b = service.crear(plato("B", 2000.0));
        service.cambiarDisponibilidad(b.getId(), false);

        List<Plato> disponibles = service.obtenerDisponibles();

        assertEquals(1, disponibles.size());
        assertEquals("A", disponibles.get(0).getNombre());
    }

    @Test
    @DisplayName("✅ obtenerPorCategoria — filtra por categoría (case-insensitive)")
    void obtenerPorCategoria_filtraCorrectamente() {
        service.crear(plato("A", 1000.0));   // categoria PRINCIPALES
        Plato postre = service.crear(plato("B", 2000.0));
        postre.setCategoria("POSTRES");

        List<Plato> postres = service.obtenerPorCategoria("postres");

        assertEquals(1, postres.size());
        assertEquals("B", postres.get(0).getNombre());
    }

    @Test
    @DisplayName("✅ cambiarDisponibilidad — desactiva un plato existente")
    void cambiarDisponibilidad_desactivaPlato() {
        Plato creado = service.crear(plato("Sopa", 15000.0));

        Plato resultado = service.cambiarDisponibilidad(creado.getId(), false);

        assertFalse(resultado.estaDisponible());
    }

    // ─── ERRORES ────────────────────────────────────────────────────────

    @Test
    @DisplayName("❌ obtenerPorId — ID inexistente lanza PlatoNotFoundException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        PlatoNotFoundException ex = assertThrows(
                PlatoNotFoundException.class,
                () -> service.obtenerPorId(999L)
        );

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("❌ crear — nombre duplicado lanza PlatoAlreadyExistsException")
    void crear_nombreDuplicado_lanzaConflicto() {
        // Configura el mock para que lance excepción al validar el nombre
        doThrow(new PlatoAlreadyExistsException("duplicado"))
                .when(validator).validarNombreUnico(any(), any());

        assertThrows(
                PlatoAlreadyExistsException.class,
                () -> service.crear(plato("Tacos", 10000.0))
        );
    }

    @Test
    @DisplayName("❌ crear — precio fuera de rango lanza IllegalArgumentException")
    void crear_precioFueraDeRango_lanzaExcepcion() {
        doThrow(new IllegalArgumentException("Precio fuera de rango"))
                .when(validator).validarPrecioRazonable(any());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.crear(plato("Caro", 999_999_999.0))
        );
    }

    @Test
    @DisplayName("❌ eliminar — ID inexistente lanza PlatoNotFoundException")
    void eliminar_noExiste_lanzaExcepcion() {
        assertThrows(
                PlatoNotFoundException.class,
                () -> service.eliminar(999L)
        );
    }

    @Test
    @DisplayName("❌ actualizar — ID inexistente lanza PlatoNotFoundException")
    void actualizar_noExiste_lanzaExcepcion() {
        assertThrows(
                PlatoNotFoundException.class,
                () -> service.actualizar(999L, plato("X", 1000.0))
        );
    }

    // ─── CASOS LÍMITE ───────────────────────────────────────────────────

    @Test
    @DisplayName("✅ obtenerTodos — lista vacía devuelve lista vacía (no null)")
    void obtenerTodos_sinPlatos_devuelveListaVacia() {
        List<Plato> resultado = service.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("✅ eliminar — elimina correctamente un plato existente")
    void eliminar_platoExistente_eliminaDelMap() {
        Plato creado = service.crear(plato("Eliminame", 1000.0));

        service.eliminar(creado.getId());

        assertThrows(
                PlatoNotFoundException.class,
                () -> service.obtenerPorId(creado.getId())
        );
    }

    @Test
    @DisplayName("✅ actualizar — modifica los campos correctamente")
    void actualizar_platoExistente_modificaCampos() {
        Plato creado = service.crear(plato("Original", 1000.0));

        Plato nuevosDatos = Plato.builder()
                .nombre("Modificado")
                .precio(2000.0)
                .categoria("POSTRES")
                .descripcion("Nueva descripción")
                .build();

        Plato resultado = service.actualizar(creado.getId(), nuevosDatos);

        assertEquals("Modificado", resultado.getNombre());
        assertEquals(2000.0, resultado.getPrecio());
        assertEquals("POSTRES", resultado.getCategoria());
    }

    @Test
    @DisplayName("eliminar — plato con pedidos activos lanza excepción")
    void eliminar_conPedidosActivos_lanzaExcepcion() {
        Plato creado = service.crear(plato("Sashimi", 22000.0));

        when(pedidoService.tienePedidosActivosConPlato(creado.getId()))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.eliminar(creado.getId()));
    }
}