package com.example.demo.config;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.type.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si ya existe el admin
            if (usuarioRepo.findByNombreUsuario("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombreUsuario("admin");
                admin.setPin(1234); // Se codificará automáticamente
                admin.setRol(Role.ROLE_ADMIN);

                usuarioRepo.save(admin);
                System.out.println("Usuario admin creado: nombreUsuario=admin, pin=1234");
            }
        };
    }
}

