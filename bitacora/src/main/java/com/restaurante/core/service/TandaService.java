package com.restaurante.core.service;

import java.util.List;

import com.restaurante.controller.dto.response.TandaResponseDTO;

public interface TandaService {
    TandaResponseDTO crear(List<Long> idsRolls);
    List<TandaResponseDTO> obtenerTodas();
}