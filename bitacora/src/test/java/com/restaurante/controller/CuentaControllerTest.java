package com.restaurante.controller;

import com.restaurante.mapper.CuentaMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.CuentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired private MockMvc       mockMvc;
    @MockBean  private CuentaService cuentaService;
    @MockBean  private CuentaMapper  cuentaMapper;

    @Test
    @DisplayName("GET /cuentas — 200")
    void listar_200() throws Exception {
        when(cuentaService.obtenerTodas()).thenReturn(List.of());
        when(cuentaMapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cuentas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /cuentas/mesa/{id} — 201")
    void abrir_201() throws Exception {
        Cuenta cuenta = Cuenta.builder().id(1L).idMesa(1L)
                .estado(EstadoCuenta.ABIERTA).total(0.0).build();
        CuentaResponseDTO dto = CuentaResponseDTO.builder()
                .id(1L).idMesa(1L).estado(EstadoCuenta.ABIERTA).build();

        when(cuentaService.abrir(1L)).thenReturn(cuenta);
        when(cuentaMapper.toResponse(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/cuentas/mesa/1"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /cuentas/999 — 404")
    void obtener_404() throws Exception {
        when(cuentaService.obtenerPorId(999L))
                .thenThrow(new com.restaurante.exception.CuentaNotFoundException("Cuenta", 999L));

        mockMvc.perform(get("/api/v1/cuentas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /cuentas/{id}/cerrar — 200")
    void cerrar_200() throws Exception {
        Cuenta cerrada = Cuenta.builder().id(1L).estado(EstadoCuenta.CERRADA).build();
        CuentaResponseDTO dto = CuentaResponseDTO.builder()
                .id(1L).estado(EstadoCuenta.CERRADA).build();

        when(cuentaService.cerrar(1L)).thenReturn(cerrada);
        when(cuentaMapper.toResponse(any())).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/cuentas/1/cerrar"))
                .andExpect(status().isOk());
    }
}