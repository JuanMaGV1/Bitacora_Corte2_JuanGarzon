package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoValidator {

    /** Regla: la transición de estado debe ser válida */
    public void validarTransicion(Pedido pedido, EstadoPedido nuevoEstado) {
        if (!pedido.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new EstadoInvalidoException(
                    "No se puede pasar de " + pedido.getEstado() +
                    " a " + nuevoEstado);
        }
    }

    /** Regla: solo se puede modificar en estado RECIBIDO */
    public void validarModificable(Pedido pedido) {
        if (!pedido.puedeModificarse()) {
            throw new EstadoInvalidoException(
                    "Un pedido en estado " + pedido.getEstado() +
                    " ya no puede modificarse");
        }
    }
}