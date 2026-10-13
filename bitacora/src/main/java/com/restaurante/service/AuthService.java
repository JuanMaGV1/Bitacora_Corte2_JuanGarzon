package com.restaurante.service;

import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroRequestDTO;
import com.restaurante.model.dto.response.TokenResponseDTO;

public interface AuthService {
    TokenResponseDTO login(LoginRequestDTO dto);
    TokenResponseDTO registrar(RegistroRequestDTO dto);
}