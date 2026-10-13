package com.restaurante.service.impl;

import com.restaurante.exception.ConflictoException;
import com.restaurante.mapper.UsuarioEntityMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.domain.Usuario;
import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroRequestDTO;
import com.restaurante.model.dto.response.TokenResponseDTO;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepository;
import com.restaurante.security.JwtUtil;
import com.restaurante.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository     usuarioRepository;
    private final UsuarioEntityMapper   usuarioMapper;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil               jwtUtil;

    @Override
    public TokenResponseDTO login(LoginRequestDTO dto) {
        // 1. Autenticar — lanza BadCredentialsException si falla
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        // 2. Buscar usuario para armar la respuesta
        UsuarioEntity usuario = usuarioRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow();

        String token = jwtUtil.generarToken(
                usuario.getEmail(),
                List.of("ROLE_" + usuario.getRol().name())
        );

        log.info("Login exitoso: {} ({})", usuario.getEmail(), usuario.getRol());

        return TokenResponseDTO.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .build();
    }

    @Override
    @Transactional
    public TokenResponseDTO registrar(RegistroRequestDTO dto) {
        if (usuarioRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ConflictoException("Ya existe un usuario con ese email");
        }

        UsuarioEntity nuevo = UsuarioEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombre(dto.getNombre())
                .rol(dto.getRol())
                .build();

        UsuarioEntity guardado = usuarioRepository.save(nuevo);
        log.info("Usuario registrado: {} ({})", guardado.getEmail(), guardado.getRol());

        String token = jwtUtil.generarToken(
                guardado.getEmail(),
                List.of("ROLE_" + guardado.getRol().name())
        );

        return TokenResponseDTO.builder()
                .token(token)
                .email(guardado.getEmail())
                .nombre(guardado.getNombre())
                .rol(guardado.getRol())
                .build();
    }
}