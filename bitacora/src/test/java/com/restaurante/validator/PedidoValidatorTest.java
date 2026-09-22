package com.restaurante.validator;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoValidatorTest {

    private final PedidoValidator validator = new PedidoValidator();

    private Pedido pedido(EstadoPedido estado, List<ItemPedido> items) {
        return Pedido.builder().estado(estado).items(items).build();
    }

    private ItemPedido item() {
        return ItemPedido.builder()
                .idPlato(1L).precioCongelado(20000.0).cantidad(1).build();
    }

    @Test
    @DisplayName("validarConfirmable — pedido RECIBIDO con items no lanza")
    void confirmable_ok() {
        Pedido p = pedido(EstadoPedido.RECIBIDO, List.of(item()));

        assertDoesNotThrow(() -> validator.validarConfirmable(p));
    }

    @Test
    @DisplayName("validarConfirmable — pedido vacío lanza excepción")
    void confirmable_vacio_lanza() {
        Pedido p = pedido(EstadoPedido.RECIBIDO, new ArrayList<>());

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarConfirmable(p));
    }

    @Test
    @DisplayName("validarConfirmable — pedido LISTO lanza excepción")
    void confirmable_listo_lanza() {
        Pedido p = pedido(EstadoPedido.LISTO, List.of(item()));

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarConfirmable(p));
    }

    @Test
    @DisplayName("validarModificable — pedido RECIBIDO es modificable")
    void modificable_recibido_ok() {
        Pedido p = pedido(EstadoPedido.RECIBIDO, List.of(item()));

        assertDoesNotThrow(() -> validator.validarModificable(p));
    }

    @Test
    @DisplayName("validarModificable — pedido ENTREGADO no es modificable")
    void modificable_entregado_lanza() {
        Pedido p = pedido(EstadoPedido.ENTREGADO, List.of(item()));

        assertThrows(PedidoNoModificableException.class,
                () -> validator.validarModificable(p));
    }

    @Test
    @DisplayName("validarTransicion — RECIBIDO → EN_PREPARACION es válida")
    void transicion_valida() {
        Pedido p = pedido(EstadoPedido.RECIBIDO, List.of(item()));

        assertDoesNotThrow(() ->
                validator.validarTransicion(p, EstadoPedido.EN_PREPARACION));
    }

    @Test
    @DisplayName("validarTransicion — RECIBIDO → ENTREGADO lanza excepción (salto)")
    void transicion_salto_lanza() {
        Pedido p = pedido(EstadoPedido.RECIBIDO, List.of(item()));

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicion(p, EstadoPedido.ENTREGADO));
    }

    @Test
    @DisplayName("validarTransicion — ENTREGADO es terminal")
    void transicion_terminal_lanza() {
        Pedido p = pedido(EstadoPedido.ENTREGADO, List.of(item()));

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicion(p, EstadoPedido.RECIBIDO));
    }
}