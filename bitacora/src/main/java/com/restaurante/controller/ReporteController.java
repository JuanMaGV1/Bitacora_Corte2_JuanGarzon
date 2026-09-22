package com.restaurante.controller;

import com.restaurante.model.dto.response.IngresoCategoriaDTO;
import com.restaurante.model.dto.response.ResumenDiaDTO;
import com.restaurante.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Inteligencia del negocio")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    @Operation(summary = "Resumen del día")
    public ResponseEntity<ResumenDiaDTO> resumen() {
        return ResponseEntity.ok(reporteService.resumenDelDia());
    }

    @GetMapping("/ingresos-por-categoria")
    @Operation(summary = "Ingresos agrupados por categoría")
    public ResponseEntity<List<IngresoCategoriaDTO>> ingresosPorCategoria() {
        return ResponseEntity.ok(reporteService.ingresosPorCategoria());
    }

    @GetMapping("/platos-populares")
    @Operation(summary = "Top N platos más pedidos")
    public ResponseEntity<List<String>> platosPopulares(
            @RequestParam(defaultValue = "5") int top) {
        return ResponseEntity.ok(reporteService.platosPopulares(top));
    }
}