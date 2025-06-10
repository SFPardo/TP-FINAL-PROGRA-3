package com.example.demo.controllers;

import com.example.demo.dto.LogInDTO;
import com.example.demo.dto.UsuarioEntradaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Usuario;
import com.example.demo.services.impl.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioServiceImpl;


    // -- Metodos GET --//
    @Operation(summary = "Obtener todos los usuarios",
    description = "Solo accesible por usuarios con rol ADMIN. Devuelve una lista de todos los usuarios.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioSalidaDTO>> obtenerTodosLosUsuariosSalida() {
        List<UsuarioSalidaDTO> usuarios = usuarioServiceImpl.obtenerTodosLosUsuariosDTO();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por ID",
        description = "Devuelve un usuario por su ID. Accesible solo por ADMIN.")
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioPorIdSalida(@PathVariable Long id) {
        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorId(id);
        if (usuario != null) {
            UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
            return ResponseEntity.ok(usuarioSalidaDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener usuario por username",
        description = "Devuelve un usuario por su nombre de usuario. Accesible solo por ADMIN.")
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioPorUsernameSalida(@PathVariable String username) {
        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorUsername(username);
        if (usuario != null) {
            UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
            return ResponseEntity.ok(usuarioSalidaDTO);
        }
        return ResponseEntity.notFound().build();
    }

    // -- Metodos PUT --//

    @Operation(summary = "Actualizar usuario por ID",
        description = "Actualiza un usuario por su ID. Accesible solo por ADMIN.")
    @PutMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntradaDTO usuarioEntradaDTO) {
        usuarioServiceImpl.actualizarUsuarioPorIdDto(id, usuarioEntradaDTO);
        Usuario usuarioActualizado = usuarioServiceImpl.buscarUsuarioPorId(id);
        return usuarioActualizado != null ? ResponseEntity.ok(usuarioActualizado) : ResponseEntity.notFound().build();
    }

    //-- Metodos DELETE--//

    @Operation(summary = "Eliminar usuario por ID",
        description = "Elimina un usuario por su ID. Accesible solo por ADMIN.")
    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioServiceImpl.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar usuario por username",
        description = "Elimina un usuario por su nombre de usuario. Accesible solo por ADMIN.")
    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuarioPorUsername(@PathVariable String username) {
        usuarioServiceImpl.eliminarUsuarioPorUsername(username);
        return ResponseEntity.noContent().build();
    }


}
