package com.example.demo.controllers;

import com.example.demo.dto.LogInDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Usuario;
import com.example.demo.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LogInDTO dto) {
        Usuario usuario = usuarioService.login(dto.getNombreUsuario(), dto.getPin());
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/cambiar-pin")
    public ResponseEntity<Usuario> cambiarPin(
            @RequestParam String nombreUsuario,
            @RequestParam int nuevoPin) {
        Usuario usuario = usuarioService.cambiarPin(nombreUsuario, nuevoPin);
        return ResponseEntity.ok(usuario);
    }
    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok(usuario);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<Usuario> obtenerUsuarioPorUsername(@PathVariable String username) {
        Usuario usuario = usuarioService.buscarUsuarioPorUsername(username);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setUsuarioId(id);
        usuarioService.actualizarUsuario(usuario);
        return ResponseEntity.ok(usuario);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> eliminarUsuarioPorUsername(@PathVariable String username) {
        usuarioService.eliminarUsuarioPorUsername(username);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> eliminarUsuarioPorEmail(@PathVariable String email) {
        usuarioService.eliminarUsuarioPorEmail(email);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> eliminarUsuarioPorId(@PathVariable Long id) {
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarUsuario(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        if (username != null) {
            Usuario usuario = usuarioService.buscarUsuarioPorUsername(username);
            return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
        } else if (email != null) {
            Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
            return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }
    @GetMapping("/buscar/nombre-usuario/{nombreUsuario}")
    public ResponseEntity<Usuario> buscarUsuarioPorNombreUsuario(@PathVariable String nombreUsuario) {
        Usuario usuario = usuarioService.buscarUsuarioPorUsername(nombreUsuario);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }
    @GetMapping("/buscar/email/{email}")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }






}
