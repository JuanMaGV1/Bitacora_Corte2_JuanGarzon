package com.restaurante.mapper;

import com.restaurante.model.domain.Ingrediente;
import com.restaurante.model.dto.request.IngredienteRequestDTO;
import com.restaurante.model.dto.response.IngredienteResponseDTO;
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