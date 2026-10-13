package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:04-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class RegistroVehiculoEntityMapperImpl implements RegistroVehiculoEntityMapper {

    @Override
    public RegistroVehiculoEntity toEntity(RegistroVehiculo registro) {
        if ( registro == null ) {
            return null;
        }

        RegistroVehiculoEntity.RegistroVehiculoEntityBuilder registroVehiculoEntity = RegistroVehiculoEntity.builder();

        registroVehiculoEntity.id( registro.getId() );
        registroVehiculoEntity.placa( registro.getPlaca() );
        registroVehiculoEntity.entrada( registro.getEntrada() );
        registroVehiculoEntity.salida( registro.getSalida() );
        registroVehiculoEntity.cobro( registro.getCobro() );

        return registroVehiculoEntity.build();
    }

    @Override
    public RegistroVehiculo toDomain(RegistroVehiculoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RegistroVehiculo.RegistroVehiculoBuilder registroVehiculo = RegistroVehiculo.builder();

        registroVehiculo.id( entity.getId() );
        registroVehiculo.placa( entity.getPlaca() );
        registroVehiculo.entrada( entity.getEntrada() );
        registroVehiculo.salida( entity.getSalida() );
        registroVehiculo.cobro( entity.getCobro() );

        return registroVehiculo.build();
    }
}
