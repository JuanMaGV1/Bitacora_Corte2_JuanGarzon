package com.restaurante.mapper;

import com.restaurante.model.domain.Ingrediente;
import com.restaurante.persistence.entity.IngredienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredienteEntityMapper {

    @Mapping(target = "creadoEn", ignore = true)
    IngredienteEntity toEntity(Ingrediente ingrediente);

    Ingrediente toDomain(IngredienteEntity entity);
}