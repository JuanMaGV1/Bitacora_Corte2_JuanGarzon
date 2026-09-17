package com.restaurante.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Personaliza el título, versión y descripción que aparece en Swagger UI.
 * La config de las rutas (/v1/api-docs) vive en application.yml.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI restauranteOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("🍽️ API Restaurante — Bitácora")
                .description("API REST del restaurante sin persistencia (en memoria)")
                .version("v1.0")
                .contact(new Contact()
                        .name("Equipo DOSW")
                        .email("dosw@eci.edu.co")));
    }
}