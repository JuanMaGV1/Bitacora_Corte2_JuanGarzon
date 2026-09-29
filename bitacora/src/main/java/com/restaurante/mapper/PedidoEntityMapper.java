package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoEntityMapper {

    /** ItemPedido → ItemPedidoEntity (el campo pedido se ignora, lo setea el Service) */
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "id", ignore = true)
    ItemPedidoEntity toEntity(ItemPedido item);

    /** ItemPedidoEntity → ItemPedido */
    ItemPedido toDomain(ItemPedidoEntity entity);

    /** Pedido → PedidoEntity (mesa se resuelve en el Service, items se manejan aparte) */
    @Mapping(target = "mesa", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    PedidoEntity toEntity(Pedido pedido);

    /** PedidoEntity → Pedido (con items convertidos por el mapper de lista) */
    @Mapping(target = "idMesa", source = "mesa.id")
    @Mapping(target = "items", source = "items")
    Pedido toDomain(PedidoEntity entity);
}