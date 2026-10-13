package com.restaurante.service;

import com.restaurante.model.dto.response.TandaResponseDTO;

import java.util.List;

public interface TandaService {
    TandaResponseDTO crear(List<Long> idsRolls);
    List<TandaResponseDTO> obtenerTodas();
}