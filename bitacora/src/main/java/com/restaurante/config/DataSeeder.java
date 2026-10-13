package com.restaurante.config;

import com.restaurante.model.domain.Rol;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder   passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("Usuarios ya existen, no se crean datos iniciales");
            return;
        }

        usuarioRepository.save(UsuarioEntity.builder()
                .email("gerente@sakura.com")
                .password(passwordEncoder.encode("gerente123"))
                .nombre("Gerente Sakura")
                .rol(Rol.GERENTE)
                .build());

        usuarioRepository.save(UsuarioEntity.builder()
                .email("mesero@sakura.com")
                .password(passwordEncoder.encode("mesero123"))
                .nombre("Mesero Sakura")
                .rol(Rol.MESERO)
                .build());

        usuarioRepository.save(UsuarioEntity.builder()
                .email("cocinero@sakura.com")
                .password(passwordEncoder.encode("cocinero123"))
                .nombre("Cocinero Sakura")
                .rol(Rol.COCINERO)
                .build());

        usuarioRepository.save(UsuarioEntity.builder()
                .email("cliente@sakura.com")
                .password(passwordEncoder.encode("cliente123"))
                .nombre("Cliente Sakura")
                .rol(Rol.CLIENTE)
                .build());

        log.info("Usuarios iniciales creados (gerente/mesero/cocinero/cliente)");
    }
}