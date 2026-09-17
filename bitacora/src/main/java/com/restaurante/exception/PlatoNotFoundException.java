package com.restaurante.exception;

/**
 * Se lanza cuando se busca un plato que no existe.
 * El GlobalExceptionHandler la traduce a un 404 Not Found.
 */
public class PlatoNotFoundException extends RuntimeException {

    public PlatoNotFoundException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor de conveniencia: recibe el recurso y el id.
     * Ejemplo: new PlatoNotFoundException("Plato", 99L)
     *         → "No existe Plato con id=99"
     */
    public PlatoNotFoundException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}