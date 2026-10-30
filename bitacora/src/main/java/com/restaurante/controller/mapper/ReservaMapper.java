package com.restaurante.controller.mapper;

import com.restaurante.controller.dto.request.ReservaRequestDTO;
import com.restaurante.controller.dto.response.ReservaResponseDTO;
import com.restaurante.core.model.domain.Reserva;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    Reserva toDomain(ReservaRequestDTO dto);

    ReservaResponseDTO toResponse(Reserva reserva);

    List<ReservaResponseDTO> toResponseList(List<Reserva> reservas);
}