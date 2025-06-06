package com.example.demo.controllers;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@AllArgsConstructor
public class TestController {
    @Autowired
    private final UsuarioRepository usuarioRepo;

    @GetMapping("/users")
    public ResponseEntity<List<Usuario>> listUsers() {
        return ResponseEntity.ok(usuarioRepo.findAll());
    }
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Acceso público permitido";
    }

}
