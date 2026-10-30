package com.restaurante.controller.mapper;

import com.restaurante.controller.dto.request.MesaRequestDTO;
import com.restaurante.controller.dto.response.MesaResponseDTO;
import com.restaurante.core.model.domain.Mesa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MesaMapper {

    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "estado",        constant = "DISPONIBLE")
    @Mapping(target = "cuentaAbierta", constant = "false")
    Mesa toDomain(MesaRequestDTO dto);

    MesaResponseDTO toResponse(Mesa mesa);

    List<MesaResponseDTO> toResponseList(List<Mesa> mesas);
}