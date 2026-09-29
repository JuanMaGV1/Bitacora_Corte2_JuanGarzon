package com.restaurante.repository;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    /** Carga TODOS los pedidos con sus items en una sola query (evita LazyInitialization) */
    @Query("SELECT DISTINCT p FROM PedidoEntity p LEFT JOIN FETCH p.items")
    List<PedidoEntity> findAllWithItems();

    /** Carga pedidos activos con items */
    @Query("""
        SELECT DISTINCT p FROM PedidoEntity p 
        LEFT JOIN FETCH p.items 
        WHERE p.estado NOT IN :estados
    """)
    List<PedidoEntity> findByEstadoNotInWithItems(@Param("estados") List<EstadoPedido> estados);

    /** Carga pedidos por mesa con items */
    @Query("SELECT DISTINCT p FROM PedidoEntity p LEFT JOIN FETCH p.items WHERE p.mesa.id = :idMesa")
    List<PedidoEntity> findByMesaIdWithItems(@Param("idMesa") Long idMesa);

    /** Carga un pedido con items por id */
    @Query("SELECT p FROM PedidoEntity p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<PedidoEntity> findByIdWithItems(@Param("id") Long id);

    /** Verifica si un plato está en algún pedido activo */
    @Query("""
        SELECT COUNT(p) > 0 FROM PedidoEntity p
        JOIN p.items i
        WHERE i.idPlato = :idPlato
          AND p.estado NOT IN ('ENTREGADO', 'CANCELADO')
    """)
    boolean existsActivePedidoConPlato(@Param("idPlato") Long idPlato);
}