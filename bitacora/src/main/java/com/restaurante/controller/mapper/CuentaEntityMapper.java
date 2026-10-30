package com.restaurante.controller.mapper;

import com.restaurante.core.model.domain.Cuenta;
import com.restaurante.persistence.entity.CuentaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuentaEntityMapper {

    @Mapping(target = "fechaApertura", ignore = true)
    CuentaEntity toEntity(Cuenta cuenta);

    Cuenta toDomain(CuentaEntity entity);

    List<Cuenta> toDomainList(List<CuentaEntity> entities);
}