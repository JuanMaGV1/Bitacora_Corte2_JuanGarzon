package com.restaurante.validator;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.model.domain.Mesa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class MesaValidator {

    /** Regla: el número de mesa debe ser único */
    public void validarNumeroUnico(Integer numero, Collection<Mesa> existentes) {
        boolean duplicado = existentes.stream()
                .anyMatch(m -> m.getNumero().equals(numero));

        if (duplicado) {
            throw new MesaNoDisponibleException(
                    "Ya existe una mesa con el número " + numero);
        }
    }

    /** Regla: no se puede abrir cuenta en una mesa no disponible */
    public void validarAperturaCuenta(Mesa mesa) {
        if (mesa.tieneCuentaAbierta()) {
            throw new MesaNoDisponibleException(
                    "La mesa " + mesa.getNumero() + " ya tiene una cuenta abierta");
        }
    }

    /** Regla: no se puede cerrar una cuenta que no está abierta */
    public void validarCierreCuenta(Mesa mesa) {
        if (!mesa.tieneCuentaAbierta()) {
            throw new MesaNoDisponibleException(
                    "La mesa " + mesa.getNumero() + " no tiene cuenta abierta");
        }
    }
}