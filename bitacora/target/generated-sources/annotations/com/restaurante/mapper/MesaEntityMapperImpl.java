package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:04-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class MesaEntityMapperImpl implements MesaEntityMapper {

    @Override
    public MesaEntity toEntity(Mesa mesa) {
        if ( mesa == null ) {
            return null;
        }

        MesaEntity.MesaEntityBuilder mesaEntity = MesaEntity.builder();

        mesaEntity.id( mesa.getId() );
        mesaEntity.numero( mesa.getNumero() );
        mesaEntity.capacidad( mesa.getCapacidad() );
        mesaEntity.estado( mesa.getEstado() );
        mesaEntity.cuentaAbierta( mesa.getCuentaAbierta() );

        return mesaEntity.build();
    }

    @Override
    public Mesa toDomain(MesaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Mesa.MesaBuilder mesa = Mesa.builder();

        mesa.id( entity.getId() );
        mesa.numero( entity.getNumero() );
        mesa.capacidad( entity.getCapacidad() );
        mesa.estado( entity.getEstado() );
        mesa.cuentaAbierta( entity.getCuentaAbierta() );

        return mesa.build();
    }
}
