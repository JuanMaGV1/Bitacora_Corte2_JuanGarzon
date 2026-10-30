package com.restaurante.controller.mapper;

import com.restaurante.core.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MesaEntityMapper {
    MesaEntity toEntity(Mesa mesa);
    Mesa toDomain(MesaEntity entity);
}