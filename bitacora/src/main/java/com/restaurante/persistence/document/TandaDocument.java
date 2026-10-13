package com.restaurante.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tandas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TandaDocument {

    @Id
    private String id;

    private List<Long>    idsRolls;
    private Integer       cantidad;
    private LocalDateTime fechaCreacion;
    private String        estado;
    private String        observaciones;
}