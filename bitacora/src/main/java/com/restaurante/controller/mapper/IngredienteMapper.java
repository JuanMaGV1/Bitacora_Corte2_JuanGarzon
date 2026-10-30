package com.restaurante.controller.mapper;

import com.restaurante.controller.dto.request.IngredienteRequestDTO;
import com.restaurante.controller.dto.response.IngredienteResponseDTO;
import com.restaurante.core.model.domain.Ingrediente;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "disponible", constant = "true")
    Ingrediente toDomain(IngredienteRequestDTO dto);

    IngredienteResponseDTO toResponse(Ingrediente ingrediente);

    List<IngredienteResponseDTO> toResponseList(List<Ingrediente> ingredientes);
}