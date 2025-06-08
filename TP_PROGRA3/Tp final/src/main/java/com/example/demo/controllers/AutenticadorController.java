package com.example.demo.controllers;


import com.example.demo.dto.LogInDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entities.Usuario;
import com.example.demo.services.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class JwtAuthResponse {
    public String accessToken;
    public String tokenType = "Bearer";

    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
@RestController
@RequestMapping("/autenticador")
public class AutenticadorController {
    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> autenticarUsuario(@Valid @RequestBody LogInDTO loginDto) {
        String token = authService.autenticarUsuario(loginDto.getNombreUsuario(), loginDto.getPin());
        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

}
