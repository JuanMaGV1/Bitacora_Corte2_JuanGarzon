package com.restaurante.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
    public RecursoNoEncontradoException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}