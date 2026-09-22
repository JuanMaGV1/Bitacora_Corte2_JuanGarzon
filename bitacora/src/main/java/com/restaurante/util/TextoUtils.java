package com.restaurante.util;

/**
 * Utilidades de texto — lógica reutilizable sin dependencias.
 */
public final class TextoUtils {

    private TextoUtils() {}   // evita instanciación

    /** Normaliza un nombre: sin espacios extra, primera letra mayúscula. */
    public static String normalizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;
        String limpio = nombre.strip().replaceAll("\\s+", " ");
        return limpio.substring(0, 1).toUpperCase() + limpio.substring(1).toLowerCase();
    }

    /** Compara dos strings ignorando mayúsculas y espacios extremos. */
    public static boolean sonIgualesNormalizados(String a, String b) {
        if (a == null || b == null) return false;
        return a.strip().equalsIgnoreCase(b.strip());
    }

    /** Genera un código con prefijo y número formateado. */
    public static String generarCodigo(String prefijo, Long id) {
        if (prefijo == null || id == null) return null;
        return prefijo + String.format("%04d", id);
    }
}