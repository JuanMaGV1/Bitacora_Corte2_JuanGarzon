package com.restaurante.core.service;

import com.restaurante.controller.dto.request.LoginRequestDTO;
import com.restaurante.controller.dto.request.RegistroRequestDTO;
import com.restaurante.controller.dto.response.TokenResponseDTO;

public interface AuthService {
    TokenResponseDTO login(LoginRequestDTO dto);
    TokenResponseDTO registrar(RegistroRequestDTO dto);
}