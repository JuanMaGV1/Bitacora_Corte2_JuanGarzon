package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:04-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class PlatoEntityMapperImpl implements PlatoEntityMapper {

    @Override
    public PlatoEntity toEntity(Plato plato) {
        if ( plato == null ) {
            return null;
        }

        PlatoEntity.PlatoEntityBuilder platoEntity = PlatoEntity.builder();

        platoEntity.id( plato.getId() );
        platoEntity.nombre( plato.getNombre() );
        platoEntity.precio( plato.getPrecio() );
        platoEntity.categoria( plato.getCategoria() );
        platoEntity.descripcion( plato.getDescripcion() );
        platoEntity.disponible( plato.getDisponible() );

        return platoEntity.build();
    }

    @Override
    public Plato toDomain(PlatoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Plato.PlatoBuilder plato = Plato.builder();

        plato.id( entity.getId() );
        plato.nombre( entity.getNombre() );
        plato.precio( entity.getPrecio() );
        plato.categoria( entity.getCategoria() );
        plato.descripcion( entity.getDescripcion() );
        plato.disponible( entity.getDisponible() );

        return plato.build();
    }
}
