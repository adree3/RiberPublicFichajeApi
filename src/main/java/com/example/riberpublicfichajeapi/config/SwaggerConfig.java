package com.example.riberpublicfichajeapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Configuración de Swagger sobre los metadatos del API
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("API RiberPublicFichaje")
                        .description("API de la aplicación de RiberPublicFichaje")
                        .contact(new Contact()
                                .name("Adrian Alonso Perez")
                                .email("adrianalonso200@gmail.com"))
                        .version("1.0"));
    }
}
