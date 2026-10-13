package com.restaurante.mapper;

import com.restaurante.model.domain.Ingrediente;
import com.restaurante.persistence.entity.IngredienteEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:03-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class IngredienteEntityMapperImpl implements IngredienteEntityMapper {

    @Override
    public IngredienteEntity toEntity(Ingrediente ingrediente) {
        if ( ingrediente == null ) {
            return null;
        }

        IngredienteEntity.IngredienteEntityBuilder ingredienteEntity = IngredienteEntity.builder();

        ingredienteEntity.id( ingrediente.getId() );
        ingredienteEntity.nombre( ingrediente.getNombre() );
        ingredienteEntity.precio( ingrediente.getPrecio() );
        ingredienteEntity.tipo( ingrediente.getTipo() );
        ingredienteEntity.disponible( ingrediente.getDisponible() );

        return ingredienteEntity.build();
    }

    @Override
    public Ingrediente toDomain(IngredienteEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Ingrediente.IngredienteBuilder ingrediente = Ingrediente.builder();

        ingrediente.id( entity.getId() );
        ingrediente.nombre( entity.getNombre() );
        ingrediente.precio( entity.getPrecio() );
        ingrediente.disponible( entity.getDisponible() );
        ingrediente.tipo( entity.getTipo() );

        return ingrediente.build();
    }
}
