package com.restaurante.core.validator;

import com.restaurante.controller.handlers.exception.ConflictoException;
import com.restaurante.controller.handlers.exception.EstadoInvalidoException;
import com.restaurante.core.model.domain.Mesa;
import com.restaurante.persistence.repository.MesaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MesaValidator {

    private final MesaRepository mesaRepository;

    public void validarNumeroUnico(Integer numero) {
        if (mesaRepository.existsByNumero(numero)) {
            throw new ConflictoException("Ya existe una mesa con el número " + numero);
        }
    }

    public void validarAperturaCuenta(Mesa mesa) {
        if (mesa.tieneCuentaAbierta()) {
            throw new EstadoInvalidoException(
                "La mesa " + mesa.getNumero() + " ya tiene una cuenta abierta");
        }
    }

    public void validarCierreCuenta(Mesa mesa) {
        if (!mesa.tieneCuentaAbierta()) {
            throw new EstadoInvalidoException(
                "La mesa " + mesa.getNumero() + " no tiene cuenta abierta");
        }
    }
}