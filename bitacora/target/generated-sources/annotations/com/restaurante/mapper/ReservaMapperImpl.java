package com.restaurante.mapper;

import com.restaurante.model.domain.EstadoReserva;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
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
public class ReservaMapperImpl implements ReservaMapper {

    @Override
    public Reserva toDomain(ReservaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Reserva.ReservaBuilder reserva = Reserva.builder();

        reserva.idMesa( dto.getIdMesa() );
        reserva.cliente( dto.getCliente() );
        reserva.fechaHora( dto.getFechaHora() );
        reserva.comensales( dto.getComensales() );

        reserva.estado( EstadoReserva.PENDIENTE );

        return reserva.build();
    }

    @Override
    public ReservaResponseDTO toResponse(Reserva reserva) {
        if ( reserva == null ) {
            return null;
        }

        ReservaResponseDTO.ReservaResponseDTOBuilder reservaResponseDTO = ReservaResponseDTO.builder();

        reservaResponseDTO.id( reserva.getId() );
        reservaResponseDTO.idMesa( reserva.getIdMesa() );
        reservaResponseDTO.cliente( reserva.getCliente() );
        reservaResponseDTO.fechaHora( reserva.getFechaHora() );
        reservaResponseDTO.comensales( reserva.getComensales() );
        reservaResponseDTO.estado( reserva.getEstado() );

        return reservaResponseDTO.build();
    }

    @Override
    public List<ReservaResponseDTO> toResponseList(List<Reserva> reservas) {
        if ( reservas == null ) {
            return null;
        }

        List<ReservaResponseDTO> list = new ArrayList<ReservaResponseDTO>( reservas.size() );
        for ( Reserva reserva : reservas ) {
            list.add( toResponse( reserva ) );
        }

        return list;
    }
}
