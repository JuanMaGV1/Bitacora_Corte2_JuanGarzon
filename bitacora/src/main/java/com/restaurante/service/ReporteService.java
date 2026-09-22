package com.restaurante.service;

import com.restaurante.model.dto.response.IngresoCategoriaDTO;
import com.restaurante.model.dto.response.ResumenDiaDTO;

import java.util.List;

public interface ReporteService {
    ResumenDiaDTO resumenDelDia();
    List<IngresoCategoriaDTO> ingresosPorCategoria();
    List<String> platosPopulares(int top);
}