package com.restaurante.core.service;

import java.util.List;

import com.restaurante.core.model.domain.Reserva;
import com.restaurante.core.model.domain.enums.EstadoReserva;

public interface ReservaService {
    List<Reserva> obtenerTodas();
    List<Reserva> obtenerPorCliente(String cliente);
    List<Reserva> obtenerVigentes();
    Reserva obtenerPorId(Long id);
    Reserva crear(Reserva reserva);
    Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado);
    void cancelar(Long id);
}