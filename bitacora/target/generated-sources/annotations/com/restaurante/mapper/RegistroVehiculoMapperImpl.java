package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
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
public class RegistroVehiculoMapperImpl implements RegistroVehiculoMapper {

    @Override
    public RegistroVehiculo toDomain(RegistroVehiculoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        RegistroVehiculo.RegistroVehiculoBuilder registroVehiculo = RegistroVehiculo.builder();

        registroVehiculo.placa( dto.getPlaca() );

        return registroVehiculo.build();
    }

    @Override
    public RegistroVehiculoResponseDTO toResponse(RegistroVehiculo registro) {
        if ( registro == null ) {
            return null;
        }

        RegistroVehiculoResponseDTO.RegistroVehiculoResponseDTOBuilder registroVehiculoResponseDTO = RegistroVehiculoResponseDTO.builder();

        registroVehiculoResponseDTO.id( registro.getId() );
        registroVehiculoResponseDTO.placa( registro.getPlaca() );
        registroVehiculoResponseDTO.entrada( registro.getEntrada() );
        registroVehiculoResponseDTO.salida( registro.getSalida() );
        registroVehiculoResponseDTO.cobro( registro.getCobro() );

        return registroVehiculoResponseDTO.build();
    }

    @Override
    public List<RegistroVehiculoResponseDTO> toResponseList(List<RegistroVehiculo> registros) {
        if ( registros == null ) {
            return null;
        }

        List<RegistroVehiculoResponseDTO> list = new ArrayList<RegistroVehiculoResponseDTO>( registros.size() );
        for ( RegistroVehiculo registroVehiculo : registros ) {
            list.add( toResponse( registroVehiculo ) );
        }

        return list;
    }
}
