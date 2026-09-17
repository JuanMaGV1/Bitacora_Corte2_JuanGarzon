package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T22:43:49-0500",
    comments = "version: 1.6.2, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class PedidoMapperImpl implements PedidoMapper {

    @Override
    public ItemPedidoResponseDTO toItemResponse(ItemPedido item) {
        if ( item == null ) {
            return null;
        }

        ItemPedidoResponseDTO.ItemPedidoResponseDTOBuilder itemPedidoResponseDTO = ItemPedidoResponseDTO.builder();

        itemPedidoResponseDTO.idPlato( item.getIdPlato() );
        itemPedidoResponseDTO.nombrePlato( item.getNombrePlato() );
        itemPedidoResponseDTO.precioCongelado( item.getPrecioCongelado() );
        itemPedidoResponseDTO.cantidad( item.getCantidad() );

        itemPedidoResponseDTO.subtotal( item.subtotal() );

        return itemPedidoResponseDTO.build();
    }

    @Override
    public PedidoResponseDTO toResponse(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoResponseDTO.PedidoResponseDTOBuilder pedidoResponseDTO = PedidoResponseDTO.builder();

        pedidoResponseDTO.items( itemPedidoListToItemPedidoResponseDTOList( pedido.getItems() ) );
        pedidoResponseDTO.id( pedido.getId() );
        pedidoResponseDTO.idMesa( pedido.getIdMesa() );
        pedidoResponseDTO.estado( pedido.getEstado() );
        pedidoResponseDTO.timestamp( pedido.getTimestamp() );
        pedidoResponseDTO.notas( pedido.getNotas() );

        pedidoResponseDTO.total( pedido.calcularTotal() );

        return pedidoResponseDTO.build();
    }

    @Override
    public List<PedidoResponseDTO> toResponseList(List<Pedido> pedidos) {
        if ( pedidos == null ) {
            return null;
        }

        List<PedidoResponseDTO> list = new ArrayList<PedidoResponseDTO>( pedidos.size() );
        for ( Pedido pedido : pedidos ) {
            list.add( toResponse( pedido ) );
        }

        return list;
    }

    protected List<ItemPedidoResponseDTO> itemPedidoListToItemPedidoResponseDTOList(List<ItemPedido> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemPedidoResponseDTO> list1 = new ArrayList<ItemPedidoResponseDTO>( list.size() );
        for ( ItemPedido itemPedido : list ) {
            list1.add( toItemResponse( itemPedido ) );
        }

        return list1;
    }
}
