package com.restaurante.mapper;

import com.restaurante.model.dto.response.TandaResponseDTO;
import com.restaurante.persistence.document.TandaDocument;
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
public class TandaDocumentMapperImpl implements TandaDocumentMapper {

    @Override
    public TandaResponseDTO toResponse(TandaDocument document) {
        if ( document == null ) {
            return null;
        }

        TandaResponseDTO.TandaResponseDTOBuilder tandaResponseDTO = TandaResponseDTO.builder();

        tandaResponseDTO.id( document.getId() );
        List<Long> list = document.getIdsRolls();
        if ( list != null ) {
            tandaResponseDTO.idsRolls( new ArrayList<Long>( list ) );
        }
        tandaResponseDTO.cantidad( document.getCantidad() );
        tandaResponseDTO.fechaCreacion( document.getFechaCreacion() );
        tandaResponseDTO.estado( document.getEstado() );

        return tandaResponseDTO.build();
    }

    @Override
    public List<TandaResponseDTO> toResponseList(List<TandaDocument> documents) {
        if ( documents == null ) {
            return null;
        }

        List<TandaResponseDTO> list = new ArrayList<TandaResponseDTO>( documents.size() );
        for ( TandaDocument tandaDocument : documents ) {
            list.add( toResponse( tandaDocument ) );
        }

        return list;
    }

    @Override
    public TandaDocument toDocument(TandaResponseDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TandaDocument.TandaDocumentBuilder tandaDocument = TandaDocument.builder();

        List<Long> list = dto.getIdsRolls();
        if ( list != null ) {
            tandaDocument.idsRolls( new ArrayList<Long>( list ) );
        }
        tandaDocument.cantidad( dto.getCantidad() );
        tandaDocument.fechaCreacion( dto.getFechaCreacion() );
        tandaDocument.estado( dto.getEstado() );

        return tandaDocument.build();
    }
}
