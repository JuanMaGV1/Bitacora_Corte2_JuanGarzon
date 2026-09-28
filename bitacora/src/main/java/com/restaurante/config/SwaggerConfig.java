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
                .title("Sakura Sushi — API REST")
                .description("""
                    API REST del restaurante japonés Sakura Sushi.

                    Barra de sushi con preparación por lotes y rolls armados a pedido.

                    Regla característica: los rolls se preparan en tandas de máximo 6 unidades.
                    """)
                .version("v1.0")
                .contact(new Contact()
                        .name("Juan Garzón — DOSW Grupo 1")
                        .email("dosw@eci.edu.co")));
    }
}