package com.restaurante.core.service;

import java.util.List;

import com.restaurante.controller.dto.response.IngresoCategoriaDTO;
import com.restaurante.controller.dto.response.ResumenDiaDTO;

public interface ReporteService {
    ResumenDiaDTO resumenDelDia();
    List<IngresoCategoriaDTO> ingresosPorCategoria();
    List<String> platosPopulares(int top);
}