package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T00:10:04-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class ReservaEntityMapperImpl implements ReservaEntityMapper {

    @Override
    public ReservaEntity toEntity(Reserva reserva) {
        if ( reserva == null ) {
            return null;
        }

        ReservaEntity.ReservaEntityBuilder reservaEntity = ReservaEntity.builder();

        reservaEntity.id( reserva.getId() );
        reservaEntity.idMesa( reserva.getIdMesa() );
        reservaEntity.cliente( reserva.getCliente() );
        reservaEntity.fechaHora( reserva.getFechaHora() );
        reservaEntity.comensales( reserva.getComensales() );
        reservaEntity.estado( reserva.getEstado() );

        return reservaEntity.build();
    }

    @Override
    public Reserva toDomain(ReservaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Reserva.ReservaBuilder reserva = Reserva.builder();

        reserva.id( entity.getId() );
        reserva.idMesa( entity.getIdMesa() );
        reserva.cliente( entity.getCliente() );
        reserva.fechaHora( entity.getFechaHora() );
        reserva.comensales( entity.getComensales() );
        reserva.estado( entity.getEstado() );

        return reserva.build();
    }
}
