package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaResponseDTO toResponse(Cuenta cuenta);

    List<CuentaResponseDTO> toResponseList(List<Cuenta> cuentas);
}