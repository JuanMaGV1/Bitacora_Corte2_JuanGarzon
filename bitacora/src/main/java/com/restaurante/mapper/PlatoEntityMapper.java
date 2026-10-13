package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatoEntityMapper {

    @Mapping(target = "creadoEn", ignore = true)
    PlatoEntity toEntity(Plato plato);

    Plato toDomain(PlatoEntity entity);
}