package com.restaurante.service;

import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;

import java.util.List;

public interface ReservaService {

    List<Reserva> obtenerTodas();
    List<Reserva> obtenerPorCliente(String cliente);
    List<Reserva> obtenerVigentes();
    Reserva obtenerPorId(Long id);
    Reserva crear(Reserva reserva);
    Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado);
    void cancelar(Long id);
}