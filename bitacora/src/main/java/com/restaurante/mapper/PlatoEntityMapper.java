package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatoEntityMapper {

    /** Dominio → Entidad JPA (para guardar en BD) */
    @Mapping(target = "creadoEn", ignore = true)
    PlatoEntity toEntity(Plato plato);

    /** Entidad JPA → Dominio (al leer de BD) */
    Plato toDomain(PlatoEntity entity);
}