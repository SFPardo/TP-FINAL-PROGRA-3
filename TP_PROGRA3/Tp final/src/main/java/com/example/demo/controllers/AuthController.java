package com.example.demo.controllers;

import com.example.demo.dto.LogInRequest;
import com.example.demo.dto.LogInResponse;
import com.example.demo.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> login(@RequestBody LogInRequest request) {

        // Autenticamos al usuario con nombre y contraseña
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNombreUsuario(),
                        request.getPin()
                )
        );

        // Obtenemos los detalles del usuario desde la base de datos
        UserDetails user = userDetailsService.loadUserByUsername(request.getNombreUsuario());

        // Generamos el token JWT
        String token = jwtService.generateToken(user);

        // Devolvemos el token en la respuesta
        return ResponseEntity.ok(new LogInResponse(token));
    }
}
