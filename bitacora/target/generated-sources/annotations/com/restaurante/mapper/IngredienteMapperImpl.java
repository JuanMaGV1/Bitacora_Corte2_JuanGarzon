package com.restaurante.mapper;

import com.restaurante.model.domain.Ingrediente;
import com.restaurante.model.dto.request.IngredienteRequestDTO;
import com.restaurante.model.dto.response.IngredienteResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:04-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class IngredienteMapperImpl implements IngredienteMapper {

    @Override
    public Ingrediente toDomain(IngredienteRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Ingrediente.IngredienteBuilder ingrediente = Ingrediente.builder();

        ingrediente.nombre( dto.getNombre() );
        ingrediente.precio( dto.getPrecio() );
        ingrediente.tipo( dto.getTipo() );

        ingrediente.disponible( true );

        return ingrediente.build();
    }

    @Override
    public IngredienteResponseDTO toResponse(Ingrediente ingrediente) {
        if ( ingrediente == null ) {
            return null;
        }

        IngredienteResponseDTO.IngredienteResponseDTOBuilder ingredienteResponseDTO = IngredienteResponseDTO.builder();

        ingredienteResponseDTO.id( ingrediente.getId() );
        ingredienteResponseDTO.nombre( ingrediente.getNombre() );
        ingredienteResponseDTO.precio( ingrediente.getPrecio() );
        ingredienteResponseDTO.tipo( ingrediente.getTipo() );
        ingredienteResponseDTO.disponible( ingrediente.getDisponible() );

        return ingredienteResponseDTO.build();
    }

    @Override
    public List<IngredienteResponseDTO> toResponseList(List<Ingrediente> ingredientes) {
        if ( ingredientes == null ) {
            return null;
        }

        List<IngredienteResponseDTO> list = new ArrayList<IngredienteResponseDTO>( ingredientes.size() );
        for ( Ingrediente ingrediente : ingredientes ) {
            list.add( toResponse( ingrediente ) );
        }

        return list;
    }
}
