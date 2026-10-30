package com.restaurante.controller.mapper;

import com.restaurante.controller.dto.request.PlatoRequestDTO;
import com.restaurante.controller.dto.response.PlatoResponseDTO;
import com.restaurante.core.model.domain.Plato;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlatoMapper {

    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "disponible", constant = "true")
    Plato toDomain(PlatoRequestDTO dto);

    PlatoResponseDTO toResponse(Plato plato);

    List<PlatoResponseDTO> toResponseList(List<Plato> platos);
}