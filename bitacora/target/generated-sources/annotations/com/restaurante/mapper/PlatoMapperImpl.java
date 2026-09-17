package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T19:24:33-0500",
    comments = "version: 1.6.2, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class PlatoMapperImpl implements PlatoMapper {

    @Override
    public Plato toDomain(PlatoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Plato.PlatoBuilder plato = Plato.builder();

        plato.categoria( dto.getCategoria() );
        plato.descripcion( dto.getDescripcion() );
        plato.nombre( dto.getNombre() );
        plato.precio( dto.getPrecio() );

        plato.disponible( true );

        return plato.build();
    }

    @Override
    public PlatoResponseDTO toResponse(Plato plato) {
        if ( plato == null ) {
            return null;
        }

        PlatoResponseDTO.PlatoResponseDTOBuilder platoResponseDTO = PlatoResponseDTO.builder();

        platoResponseDTO.categoria( plato.getCategoria() );
        platoResponseDTO.descripcion( plato.getDescripcion() );
        platoResponseDTO.disponible( plato.getDisponible() );
        platoResponseDTO.id( plato.getId() );
        platoResponseDTO.nombre( plato.getNombre() );
        platoResponseDTO.precio( plato.getPrecio() );

        return platoResponseDTO.build();
    }

    @Override
    public List<PlatoResponseDTO> toResponseList(List<Plato> platos) {
        if ( platos == null ) {
            return null;
        }

        List<PlatoResponseDTO> list = new ArrayList<PlatoResponseDTO>( platos.size() );
        for ( Plato plato : platos ) {
            list.add( toResponse( plato ) );
        }

        return list;
    }
}
