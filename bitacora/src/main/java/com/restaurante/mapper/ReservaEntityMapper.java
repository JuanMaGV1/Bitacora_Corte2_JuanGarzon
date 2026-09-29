package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaEntityMapper {

    @Mapping(target = "creadoEn", ignore = true)
    ReservaEntity toEntity(Reserva reserva);

    Reserva toDomain(ReservaEntity entity);
}