package com.example.demo.services.impl;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.repositories.CredencialRepository;
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
    private CredencialRepository credentialRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectado para hashear el PIN
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public String autenticarUsuario(String nombreUsuario, String pin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nombreUsuario, pin)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }
}
