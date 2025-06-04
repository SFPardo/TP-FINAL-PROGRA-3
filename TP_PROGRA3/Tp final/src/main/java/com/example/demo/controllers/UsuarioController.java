package com.example.demo.controllers;

import com.example.demo.dto.LogInDTO;
import com.example.demo.dto.UsuarioEntradaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Usuario;
import com.example.demo.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioServiceImpl;

    @GetMapping
    public ResponseEntity<List<UsuarioSalidaDTO>> obtenerTodosLosUsuariosSalida() {
        List<UsuarioSalidaDTO> usuarios = usuarioServiceImpl.obtenerTodosLosUsuariosDTO();
        return ResponseEntity.ok(usuarios);
    }
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LogInDTO dto) {
        Usuario usuario = usuarioServiceImpl.login(dto.getNombreUsuario(), dto.getPin());
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/cambiar-pin")
    public ResponseEntity<Usuario> cambiarPin(
            @RequestParam String nombreUsuario,
            @RequestParam int nuevoPin) {
        Usuario usuario = usuarioServiceImpl.cambiarPin(nombreUsuario, nuevoPin);
        return ResponseEntity.ok(usuario);
    }
    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuarioDto(@RequestBody UsuarioEntradaDTO usuarioEntradaDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioEntradaDTO.getNombreUsuario());
        usuario.setPin(usuarioEntradaDTO.getPin());
        usuarioServiceImpl.crearUsuario(usuario);
        return ResponseEntity.ok(usuario);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioPorIdSalida(@PathVariable Long id) {
        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorId(id);
        if (usuario != null) {
            UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
            return ResponseEntity.ok(usuarioSalidaDTO);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioPorUsernameSalida(@PathVariable String username) {
        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorUsername(username);
        if (usuario != null) {
            UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
            return ResponseEntity.ok(usuarioSalidaDTO);
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntradaDTO usuarioEntradaDTO) {
        usuarioServiceImpl.actualizarUsuarioPorIdDto(id, usuarioEntradaDTO);
        Usuario usuarioActualizado = usuarioServiceImpl.buscarUsuarioPorId(id);
        return usuarioActualizado != null ? ResponseEntity.ok(usuarioActualizado) : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioServiceImpl.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> eliminarUsuarioPorUsername(@PathVariable String username) {
        usuarioServiceImpl.eliminarUsuarioPorUsername(username);
        return ResponseEntity.noContent().build();
    }



}
