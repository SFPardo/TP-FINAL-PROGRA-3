package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permite solicitudes desde el origen de tu aplicación Angular
        registry.addMapping("/**") // Aplica a todos los endpoints (/**)
                .allowedOrigins("http://localhost:4200") // 🛑 Origen de Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("*") // Permite todos los encabezados
                .allowCredentials(true) // Permite credenciales (necesario para JWT en algunos casos)
                .maxAge(3600); // Duración de la caché de pre-vuelo (preflight)
    }
}