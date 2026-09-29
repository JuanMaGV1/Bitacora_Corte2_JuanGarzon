package com.restaurante.repository;

import com.restaurante.persistence.document.TandaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TandaRepository extends MongoRepository<TandaDocument, String> {

    /** Extiende MongoRepository — igual que JpaRepository pero para documentos. */
    
    List<TandaDocument> findByEstado(String estado);
    List<TandaDocument> findByFechaCreacionBetween(LocalDateTime desde, LocalDateTime hasta);
    List<TandaDocument> findByCantidadLessThanEqual(int cantidad);
}