package com.restaurante.controller.mapper;

import com.restaurante.controller.dto.response.CuentaResponseDTO;
import com.restaurante.core.model.domain.Cuenta;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuentaMapper {
    CuentaResponseDTO toResponse(Cuenta cuenta);
    List<CuentaResponseDTO> toResponseList(List<Cuenta> cuentas);
}