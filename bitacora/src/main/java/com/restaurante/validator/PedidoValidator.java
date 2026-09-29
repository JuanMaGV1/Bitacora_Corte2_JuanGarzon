package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoValidator {

    public void validarConfirmable(Pedido pedido) {
        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            throw new EstadoInvalidoException("No se puede confirmar un pedido sin rolls");
        }
        if (pedido.getEstado() != EstadoPedido.RECIBIDO) {
            throw new EstadoInvalidoException(
                    "Solo se pueden confirmar pedidos en estado RECIBIDO. Estado actual: "
                    + pedido.getEstado());
        }
    }

    public void validarModificable(Pedido pedido) {
        if (!pedido.puedeModificarse()) {
            throw new PedidoNoModificableException(
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