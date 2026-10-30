package com.restaurante.controller.mapper;

import com.restaurante.core.model.domain.Ingrediente;
import com.restaurante.persistence.entity.IngredienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredienteEntityMapper {

    @Mapping(target = "creadoEn", ignore = true)
    IngredienteEntity toEntity(Ingrediente ingrediente);

    Ingrediente toDomain(IngredienteEntity entity);
}