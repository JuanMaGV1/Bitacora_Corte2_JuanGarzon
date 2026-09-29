package com.restaurante.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento MongoDB para las tandas de Sakura Sushi.
 * 
 * MongoDB no requiere tabla separada para la lista de rolls —
 * los IDs van embebidos directamente en el documento.
 * Esto es más eficiente porque los rolls SIEMPRE se consultan junto con la tanda.
 */
@Document(collection = "tandas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TandaDocument {

    @Id
    private String id;                       // Mongo usa String por defecto (ObjectId)

    private List<Long> idsRolls;             // IDs embebidos — no requiere tabla intermedia

    private Integer cantidad;
    private LocalDateTime fechaCreacion;
    private String estado;
    private String observaciones;            // Campo opcional — Mongo lo permite fácil
}