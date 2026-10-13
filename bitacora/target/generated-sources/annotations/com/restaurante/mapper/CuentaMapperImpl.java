package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
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
public class CuentaMapperImpl implements CuentaMapper {

    @Override
    public CuentaResponseDTO toResponse(Cuenta cuenta) {
        if ( cuenta == null ) {
            return null;
        }

        CuentaResponseDTO.CuentaResponseDTOBuilder cuentaResponseDTO = CuentaResponseDTO.builder();

        cuentaResponseDTO.id( cuenta.getId() );
        cuentaResponseDTO.idMesa( cuenta.getIdMesa() );
        List<Long> list = cuenta.getIdsPedidos();
        if ( list != null ) {
            cuentaResponseDTO.idsPedidos( new ArrayList<Long>( list ) );
        }
        cuentaResponseDTO.total( cuenta.getTotal() );
        cuentaResponseDTO.estado( cuenta.getEstado() );
        cuentaResponseDTO.fechaApertura( cuenta.getFechaApertura() );

        return cuentaResponseDTO.build();
    }

    @Override
    public List<CuentaResponseDTO> toResponseList(List<Cuenta> cuentas) {
        if ( cuentas == null ) {
            return null;
        }

        List<CuentaResponseDTO> list = new ArrayList<CuentaResponseDTO>( cuentas.size() );
        for ( Cuenta cuenta : cuentas ) {
            list.add( toResponse( cuenta ) );
        }

        return list;
    }
}
