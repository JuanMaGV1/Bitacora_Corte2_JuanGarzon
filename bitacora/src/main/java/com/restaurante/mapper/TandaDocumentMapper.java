package com.restaurante.mapper;

import com.restaurante.model.dto.response.TandaResponseDTO;
import com.restaurante.persistence.document.TandaDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TandaDocumentMapper {

    TandaResponseDTO toResponse(TandaDocument document);

    List<TandaResponseDTO> toResponseList(List<TandaDocument> documents);

    @Mapping(target = "id", ignore = true)
    TandaDocument toDocument(TandaResponseDTO dto);
}