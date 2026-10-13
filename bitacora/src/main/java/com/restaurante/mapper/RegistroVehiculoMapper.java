package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
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