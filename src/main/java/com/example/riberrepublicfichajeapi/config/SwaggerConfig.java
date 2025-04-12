package com.example.riberrepublicfichajeapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SwaggerConfig {

    // Configuración de Swagger/OpenAPI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("API RiberRepublicFichaje")
                        .description("API de la aplicación de RiberRepublicFichaje")
                        .contact(new Contact()
                                .name("Adrian")
                                .email("adrianalonso200@gmail.com")
                                .url("https://adrian.com"))
                        .version("1.0"));
    }

    // Configuración de seguridad para desactivar autenticación
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
