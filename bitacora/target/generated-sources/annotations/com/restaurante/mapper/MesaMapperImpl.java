package com.restaurante.mapper;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
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
public class MesaMapperImpl implements MesaMapper {

    @Override
    public Mesa toDomain(MesaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Mesa.MesaBuilder mesa = Mesa.builder();

        mesa.numero( dto.getNumero() );
        mesa.capacidad( dto.getCapacidad() );

        mesa.estado( EstadoMesa.DISPONIBLE );
        mesa.cuentaAbierta( false );

        return mesa.build();
    }

    @Override
    public MesaResponseDTO toResponse(Mesa mesa) {
        if ( mesa == null ) {
            return null;
        }

        MesaResponseDTO.MesaResponseDTOBuilder mesaResponseDTO = MesaResponseDTO.builder();

        mesaResponseDTO.id( mesa.getId() );
        mesaResponseDTO.numero( mesa.getNumero() );
        mesaResponseDTO.capacidad( mesa.getCapacidad() );
        mesaResponseDTO.estado( mesa.getEstado() );
        mesaResponseDTO.cuentaAbierta( mesa.getCuentaAbierta() );

        return mesaResponseDTO.build();
    }

    @Override
    public List<MesaResponseDTO> toResponseList(List<Mesa> mesas) {
        if ( mesas == null ) {
            return null;
        }

        List<MesaResponseDTO> list = new ArrayList<MesaResponseDTO>( mesas.size() );
        for ( Mesa mesa : mesas ) {
            list.add( toResponse( mesa ) );
        }

        return list;
    }
}
