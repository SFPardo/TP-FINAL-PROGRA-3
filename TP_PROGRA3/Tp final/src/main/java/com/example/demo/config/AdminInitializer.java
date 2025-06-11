package com.example.demo.config;

import com.example.demo.entities.Credencial;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.enums.TipoRol;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        final String adminUsername = "admin"; // Nombre de usuario para el admin
        final String adminPin = "1234";      // PIN para el admin

        // Verifica si el usuario administrador ya existe
        if (usuarioRepository.findByNombreUsuario(adminUsername).isEmpty()) {
            System.out.println("Creando usuario administrador inicial...");

            // Crea la credencial
            Credencial credencialAdmin = new Credencial();
            credencialAdmin.setPin(passwordEncoder.encode(adminPin)); // Cifra el PIN

            // Crea el usuario
            Usuario adminUser = new Usuario();
            adminUser.setNombreUsuario(adminUsername);
            adminUser.setRol(TipoRol.ADMIN); // Asigna el rol ADMIN
            adminUser.setCredencial(credencialAdmin); // Asigna la credencial al usuario
            credencialAdmin.setUsuario(adminUser); // Establece la relación bidireccional

            // Guarda el usuario en la base de datos
            usuarioRepository.save(adminUser);

            System.out.println("Usuario administrador '" + adminUsername + "' creado exitosamente.");
        } else {
            System.out.println("El usuario administrador '" + adminUsername + "' ya existe. No se creará uno nuevo.");
        }
    }
}
