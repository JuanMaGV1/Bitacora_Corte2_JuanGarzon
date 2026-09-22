package com.restaurante.service;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.IngresoCategoriaDTO;
import com.restaurante.model.dto.response.ResumenDiaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final PedidoService pedidoService;
    private final PlatoService  platoService;
    private final CuentaService cuentaService;

    @Override
    public ResumenDiaDTO resumenDelDia() {
        List<Pedido> pedidos = pedidoService.obtenerTodos();

        long totalPedidos = pedidos.size();

        double ingresoTotal = pedidos.stream()
                .mapToDouble(Pedido::calcularTotal)
                .sum();

        Map<String, Long> platosPopulares = pedidos.stream()
                .flatMap(p -> p.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getNombrePlato(),
                        Collectors.counting()
                ));

        String platoMasPedido = platosPopulares.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin pedidos");

        long mesasConCuentaAbierta = cuentaService.obtenerTodas().stream()
                .filter(Cuenta::estaAbierta)
                .count();

        log.info("Resumen del día: pedidos={}, ingresos=${}",
                totalPedidos, ingresoTotal);

        return ResumenDiaDTO.builder()
                .totalPedidos(totalPedidos)
                .ingresoTotal(ingresoTotal)
                .platoMasPedido(platoMasPedido)
                .mesasConCuentaAbierta(mesasConCuentaAbierta)
                .platosPopulares(platosPopulares)
                .build();
    }

    @Override
    public List<IngresoCategoriaDTO> ingresosPorCategoria() {
        return platoService.obtenerTodos().stream()
                .collect(Collectors.groupingBy(Plato::getCategoria))
                .entrySet().stream()
                .map(e -> IngresoCategoriaDTO.builder()
                        .categoria(e.getKey())
                        .cantidad((long) e.getValue().size())
                        .ingreso(e.getValue().stream()
                                .mapToDouble(Plato::getPrecio)
                                .sum())
                        .build())
                .toList();
    }

    @Override
    public List<String> platosPopulares(int top) {
        return pedidoService.obtenerTodos().stream()
                .flatMap(p -> p.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getNombrePlato(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .map(Map.Entry::getKey)
                .toList();
    }
}