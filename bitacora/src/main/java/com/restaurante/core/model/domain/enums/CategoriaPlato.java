package com.restaurante.core.model.domain.enums;

public enum CategoriaPlato {
    ROLL, SASHIMI, NIGIRI, ENTRADA, BEBIDA, POSTRE;

    public boolean esRoll() { return this == ROLL; }
}