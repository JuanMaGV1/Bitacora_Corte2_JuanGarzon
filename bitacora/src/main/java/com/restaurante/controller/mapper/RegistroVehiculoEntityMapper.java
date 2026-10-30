package com.restaurante.controller.mapper;

import com.restaurante.core.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoEntityMapper {
    RegistroVehiculoEntity toEntity(RegistroVehiculo registro);
    RegistroVehiculo toDomain(RegistroVehiculoEntity entity);
}