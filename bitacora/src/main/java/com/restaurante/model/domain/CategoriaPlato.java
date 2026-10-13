package com.restaurante.model.domain;

public enum CategoriaPlato {
    ROLL, SASHIMI, NIGIRI, ENTRADA, BEBIDA, POSTRE;

    public boolean esRoll() { return this == ROLL; }
}