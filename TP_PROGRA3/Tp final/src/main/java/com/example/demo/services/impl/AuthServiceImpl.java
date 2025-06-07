package com.example.demo.services.impl;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.Credencial;
import com.example.demo.entities.enums.TipoRol;
import com.example.demo.repositories.CredentialRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectado para hashear el PIN
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public String autenticarUsuario(String nombreUsuario, String pin) {
        // password aquí es el PIN en texto plano que el usuario envió
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nombreUsuario, pin)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    public Usuario registrarUsuario(String nombreUsuario, String pin, String rol) {
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new RuntimeException("El usuario ya existe: " + nombreUsuario);
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        Credencial credencial = new Credencial();
        credencial.setPin(passwordEncoder.encode(pin));
        credencial.setUsuario(usuario);

        TipoRol rolUsuario;
        if ("cliente".equalsIgnoreCase(rol)) {
            rolUsuario = TipoRol.CLIENTE;
        } else if ("admin".equalsIgnoreCase(rol)) {
            rolUsuario = TipoRol.ADMIN;
        } else {
            throw new IllegalArgumentException("El rol no existe: " + rol);
        }
        usuario.setRol(rolUsuario);
        usuario.setCredencial(credencial);

        usuarioRepository.save(usuario);
        credentialRepository.save(credencial);

        return usuario;
    }
}
