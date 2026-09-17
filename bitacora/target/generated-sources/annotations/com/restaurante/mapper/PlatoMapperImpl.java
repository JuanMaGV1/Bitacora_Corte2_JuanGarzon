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
    date = "2026-09-16T22:30:46-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class PlatoMapperImpl implements PlatoMapper {

    @Override
    public Plato toDomain(PlatoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Plato.PlatoBuilder plato = Plato.builder();

        plato.nombre( dto.getNombre() );
        plato.precio( dto.getPrecio() );
        plato.categoria( dto.getCategoria() );
        plato.descripcion( dto.getDescripcion() );

        plato.disponible( true );

        return plato.build();
    }

    @Override
    public PlatoResponseDTO toResponse(Plato plato) {
        if ( plato == null ) {
            return null;
        }

        PlatoResponseDTO.PlatoResponseDTOBuilder platoResponseDTO = PlatoResponseDTO.builder();

        platoResponseDTO.id( plato.getId() );
        platoResponseDTO.nombre( plato.getNombre() );
        platoResponseDTO.precio( plato.getPrecio() );
        platoResponseDTO.categoria( plato.getCategoria() );
        platoResponseDTO.descripcion( plato.getDescripcion() );
        platoResponseDTO.disponible( plato.getDisponible() );

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
