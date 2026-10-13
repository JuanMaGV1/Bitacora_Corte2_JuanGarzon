package com.restaurante.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI restauranteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sakura Sushi — API REST")
                        .description("""
                            API REST del restaurante japonés Sakura Sushi.

                            **Cómo autenticarse:**
                            1. Ejecuta `POST /auth/login` con las credenciales del seeder.
                            2. Copia el campo `token` de la respuesta.
                            3. Haz clic en **Authorize** (arriba a la derecha) y pega el token.
                            4. Todas las peticiones incluirán el header `Authorization: Bearer <token>`.
                            """)
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Juan Garzón — DOSW Grupo 1")
                                .email("dosw@eci.edu.co")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}