package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct. Spring lo registra como bean gracias a componentModel="spring".
 * Se inyecta en el Controller (nunca en el Service).
 */
@Mapper(componentModel = "spring")
public interface PlatoMapper {

    /** MapperIn: RequestDTO → Dominio
     *  - id se ignora (lo asigna el Service)
     *  - disponible se inicializa en true (todo plato nuevo empieza disponible)
     */
    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "disponible", constant = "true")
    Plato toDomain(PlatoRequestDTO dto);

    /** MapperOut: Dominio → ResponseDTO */
    PlatoResponseDTO toResponse(Plato plato);

    /** Conversión de listas — MapStruct genera el bucle automáticamente */
    List<PlatoResponseDTO> toResponseList(List<Plato> platos);
}