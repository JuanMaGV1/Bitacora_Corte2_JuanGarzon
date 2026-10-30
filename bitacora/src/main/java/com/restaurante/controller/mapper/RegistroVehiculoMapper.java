package com.restaurante.controller.mapper;

import com.restaurante.controller.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.controller.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.core.model.domain.RegistroVehiculo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoMapper {

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "entrada", ignore = true)
    @Mapping(target = "salida",  ignore = true)
    @Mapping(target = "cobro",   ignore = true)
    RegistroVehiculo toDomain(RegistroVehiculoRequestDTO dto);

    RegistroVehiculoResponseDTO toResponse(RegistroVehiculo registro);

    List<RegistroVehiculoResponseDTO> toResponseList(List<RegistroVehiculo> registros);
}