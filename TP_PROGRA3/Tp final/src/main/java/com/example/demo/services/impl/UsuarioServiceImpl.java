package com.example.demo.services.impl;

import com.example.demo.dto.UsuarioEntradaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private final UsuarioRepository usuarioRepository;

    public void crearUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    public Usuario buscarUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }
    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
    public void actualizarUsuarioPorIdDto(Long id, UsuarioEntradaDTO usuarioEntradaDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setNombreUsuario(usuarioEntradaDTO.getNombreUsuario());
        usuario.setPin(usuarioEntradaDTO.getPin());
        usuarioRepository.save(usuario);
    }
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    public void eliminarUsuarioPorUsername(String username) {
        Usuario usuario = buscarUsuarioPorUsername(username);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }
    public void eliminarUsuarioPorEmail(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }
    public void eliminarUsuarioPorId(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario login(String nombreUsuario, int pin) {
        return usuarioRepository
                .findNombreUsuarioAndPin(nombreUsuario, pin)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }
    public Usuario cambiarPin(String nombreUsuario, int nuevoPin) {
        Usuario usuario = usuarioRepository
                .findByUsername(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPin(nuevoPin);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public static UsuarioSalidaDTO mapToDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
    }

    public List<UsuarioSalidaDTO> mapToDtoList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioServiceImpl::mapToDto)
                .toList();
    }
    public List<UsuarioSalidaDTO> obtenerTodosLosUsuariosDTO() {
        return mapToDtoList(usuarioRepository.findAll());
    }
}
