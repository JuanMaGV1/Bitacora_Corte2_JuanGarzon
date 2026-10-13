package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
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
public class PedidoEntityMapperImpl implements PedidoEntityMapper {

    @Override
    public ItemPedidoEntity toEntity(ItemPedido item) {
        if ( item == null ) {
            return null;
        }

        ItemPedidoEntity.ItemPedidoEntityBuilder itemPedidoEntity = ItemPedidoEntity.builder();

        itemPedidoEntity.idPlato( item.getIdPlato() );
        itemPedidoEntity.nombrePlato( item.getNombrePlato() );
        itemPedidoEntity.precioCongelado( item.getPrecioCongelado() );
        itemPedidoEntity.cantidad( item.getCantidad() );

        return itemPedidoEntity.build();
    }

    @Override
    public ItemPedido toDomain(ItemPedidoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ItemPedido.ItemPedidoBuilder itemPedido = ItemPedido.builder();

        itemPedido.idPlato( entity.getIdPlato() );
        itemPedido.nombrePlato( entity.getNombrePlato() );
        itemPedido.precioCongelado( entity.getPrecioCongelado() );
        itemPedido.cantidad( entity.getCantidad() );

        return itemPedido.build();
    }

    @Override
    public PedidoEntity toEntity(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoEntity.PedidoEntityBuilder pedidoEntity = PedidoEntity.builder();

        pedidoEntity.id( pedido.getId() );
        pedidoEntity.estado( pedido.getEstado() );
        pedidoEntity.notas( pedido.getNotas() );

        return pedidoEntity.build();
    }

    @Override
    public Pedido toDomain(PedidoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Pedido.PedidoBuilder pedido = Pedido.builder();

        pedido.idMesa( entityMesaId( entity ) );
        pedido.items( itemPedidoEntityListToItemPedidoList( entity.getItems() ) );
        pedido.id( entity.getId() );
        pedido.estado( entity.getEstado() );
        pedido.timestamp( entity.getTimestamp() );
        pedido.notas( entity.getNotas() );

        return pedido.build();
    }

    private Long entityMesaId(PedidoEntity pedidoEntity) {
        MesaEntity mesa = pedidoEntity.getMesa();
        if ( mesa == null ) {
            return null;
        }
        return mesa.getId();
    }

    protected List<ItemPedido> itemPedidoEntityListToItemPedidoList(List<ItemPedidoEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemPedido> list1 = new ArrayList<ItemPedido>( list.size() );
        for ( ItemPedidoEntity itemPedidoEntity : list ) {
            list1.add( toDomain( itemPedidoEntity ) );
        }

        return list1;
    }
}
