package com.restaurante.model.domain;

/**
 * Categorías de platos en Sakura Sushi.
 * ROLL es especial: se prepara por tandas de máximo 6 unidades.
 */
public enum CategoriaPlato {
    ROLL,
    SASHIMI,
    NIGIRI,
    ENTRADA,
    BEBIDA,
    POSTRE;

    public boolean esRoll() {
        return this == ROLL;
    }
}