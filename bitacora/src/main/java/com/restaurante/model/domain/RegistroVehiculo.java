package com.restaurante.model.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 

public class RegistroVehiculo {
    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double cobro;

    public double calcularCobro(){return cobro;}
    public void registrarSalida(){}
    public boolean estaActivo(){return false;}

}
