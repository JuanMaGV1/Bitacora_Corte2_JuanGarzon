package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.persistence.entity.CuentaEntity;
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
public class CuentaEntityMapperImpl implements CuentaEntityMapper {

    @Override
    public CuentaEntity toEntity(Cuenta cuenta) {
        if ( cuenta == null ) {
            return null;
        }

        CuentaEntity.CuentaEntityBuilder cuentaEntity = CuentaEntity.builder();

        cuentaEntity.id( cuenta.getId() );
        cuentaEntity.idMesa( cuenta.getIdMesa() );
        List<Long> list = cuenta.getIdsPedidos();
        if ( list != null ) {
            cuentaEntity.idsPedidos( new ArrayList<Long>( list ) );
        }
        cuentaEntity.total( cuenta.getTotal() );
        cuentaEntity.estado( cuenta.getEstado() );

        return cuentaEntity.build();
    }

    @Override
    public Cuenta toDomain(CuentaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Cuenta.CuentaBuilder cuenta = Cuenta.builder();

        cuenta.id( entity.getId() );
        cuenta.idMesa( entity.getIdMesa() );
        List<Long> list = entity.getIdsPedidos();
        if ( list != null ) {
            cuenta.idsPedidos( new ArrayList<Long>( list ) );
        }
        cuenta.total( entity.getTotal() );
        cuenta.estado( entity.getEstado() );
        cuenta.fechaApertura( entity.getFechaApertura() );

        return cuenta.build();
    }

    @Override
    public List<Cuenta> toDomainList(List<CuentaEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Cuenta> list = new ArrayList<Cuenta>( entities.size() );
        for ( CuentaEntity cuentaEntity : entities ) {
            list.add( toDomain( cuentaEntity ) );
        }

        return list;
    }
}
