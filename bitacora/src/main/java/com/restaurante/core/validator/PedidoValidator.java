package com.restaurante.core.validator;

import com.restaurante.controller.handlers.exception.EstadoInvalidoException;
import com.restaurante.core.model.domain.Pedido;
import com.restaurante.core.model.domain.enums.EstadoPedido;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoValidator {

    public void validarModificable(Pedido pedido) {
        if (!pedido.puedeModificarse()) {
            throw new EstadoInvalidoException(
                "Un pedido en estado " + pedido.getEstado() + " ya no puede modificarse");
        }
    }

    public void validarTransicion(Pedido pedido, EstadoPedido nuevoEstado) {
        if (!pedido.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new EstadoInvalidoException(
                "No se puede pasar de " + pedido.getEstado() + " a " + nuevoEstado);
        }
    }
}