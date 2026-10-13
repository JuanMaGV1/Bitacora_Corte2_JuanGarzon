package com.restaurante.service.impl;

import com.restaurante.exception.ConflictoException;
import com.restaurante.mapper.UsuarioEntityMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroRequestDTO;
import com.restaurante.model.dto.response.TokenResponseDTO;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepository;
import com.restaurante.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl — pruebas unitarias")
class AuthServiceImplTest {

    @Mock private UsuarioRepository   usuarioRepository;
    @Mock private UsuarioEntityMapper usuarioMapper;
    @Mock private PasswordEncoder     passwordEncoder;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil             jwtUtil;

    @InjectMocks private AuthServiceImpl service;

    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = UsuarioEntity.builder()
                .id(1L).email("gerente@sakura.com")
                .password("$2a$10$hashed").nombre("Gerente")
                .rol(Rol.GERENTE).build();
    }

    @Test
    @DisplayName("login — credenciales válidas devuelve token")
    void login_ok() {
        LoginRequestDTO dto = new LoginRequestDTO("gerente@sakura.com", "gerente123");
        Authentication auth = mock(Authentication.class);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioRepository.findByEmailIgnoreCase("gerente@sakura.com"))
                .thenReturn(Optional.of(usuario));
        when(jwtUtil.generarToken(eq("gerente@sakura.com"), any()))
                .thenReturn("token-jwt-fake");

        TokenResponseDTO resultado = service.login(dto);

        assertNotNull(resultado.getToken());
        assertEquals("gerente@sakura.com", resultado.getEmail());
        assertEquals(Rol.GERENTE, resultado.getRol());
    }

    @Test
    @DisplayName("registrar — email nuevo guarda usuario y devuelve token")
    void registrar_ok() {
        RegistroRequestDTO dto = new RegistroRequestDTO(
                "nuevo@sakura.com", "password123", "Nuevo", Rol.CLIENTE);

        UsuarioEntity guardado = UsuarioEntity.builder()
                .id(2L).email("nuevo@sakura.com")
                .password("$2a$10$hashed").nombre("Nuevo")
                .rol(Rol.CLIENTE).build();

        when(usuarioRepository.existsByEmailIgnoreCase("nuevo@sakura.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashed");
        when(usuarioRepository.save(any())).thenReturn(guardado);
        when(jwtUtil.generarToken(eq("nuevo@sakura.com"), any()))
                .thenReturn("token-jwt-fake");

        TokenResponseDTO resultado = service.registrar(dto);

        assertEquals("nuevo@sakura.com", resultado.getEmail());
        assertEquals(Rol.CLIENTE, resultado.getRol());
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("registrar — email duplicado lanza ConflictoException")
    void registrar_duplicado_lanza() {
        RegistroRequestDTO dto = new RegistroRequestDTO(
                "gerente@sakura.com", "password123", "Gerente", Rol.GERENTE);

        when(usuarioRepository.existsByEmailIgnoreCase("gerente@sakura.com")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.registrar(dto));
        verify(usuarioRepository, never()).save(any());
    }
}