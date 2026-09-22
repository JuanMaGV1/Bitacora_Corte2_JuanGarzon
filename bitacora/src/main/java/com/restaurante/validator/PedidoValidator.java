package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Reglas de negocio del flujo de pedidos de Sakura Sushi.
 */
@Slf4j
@Component
public class PedidoValidator {

    /** SS-05: no se puede confirmar un pedido vacío */
    public void validarConfirmable(Pedido pedido) {
        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            throw new EstadoInvalidoException(
                    "No se puede confirmar un pedido sin rolls");
        }
        if (pedido.getEstado() != EstadoPedido.RECIBIDO) {
            throw new EstadoInvalidoException(
                    "Solo se pueden confirmar pedidos en estado RECIBIDO. Estado actual: "
                    + pedido.getEstado());
        }
    }

    /** SS-02/03/04: solo modificables en RECIBIDO */
    public void validarModificable(Pedido pedido) {
        if (!pedido.puedeModificarse()) {
            throw new PedidoNoModificableException(
                    "Un pedido en estado " + pedido.getEstado()
                    + " ya no puede modificarse");
        }
    }

    /** SS-06: transiciones válidas */
    public void validarTransicion(Pedido pedido, EstadoPedido nuevoEstado) {
        if (!pedido.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new EstadoInvalidoException(
                    "No se puede pasar de " + pedido.getEstado()
                    + " a " + nuevoEstado);
        }
    }
}