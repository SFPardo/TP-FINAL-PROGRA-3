package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.JwtService;
import com.example.demo.services.SecurityService;
import com.example.demo.services.UsuarioService;
import com.example.demo.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public void crearUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    public Usuario buscarUsuarioPorUsername(String username) {
        return usuarioRepository.findByNombreUsuario(username).orElse(null);
    }
    public void actualizarUsuario(Usuario usuario) {
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
    public void eliminarUsuarioPorId(Long id) {
        usuarioRepository.deleteById(id);
    }


    public Usuario login(String nombreUsuario, int pin) {
        return usuarioRepository
                .findByNombreUsuarioAndPin(nombreUsuario, pin)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }
    public AuthResponse login(LogInRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getNombreUsuario(),
                        String.valueOf(loginRequest.getPin())
                ));

        Usuario usuario = usuarioRepository.findByNombreUsuario(loginRequest.getNombreUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .nombreUsuario(usuario.getNombreUsuario())
                .rol(usuario.getRol().name())
                .usuarioId(usuario.getUsuarioId())
                .build();
    }

    public Usuario crearUsuarioConRol(UsuarioEntradaDTO usuarioEntradaDTO) {
        if (usuarioRepository.findByNombreUsuario(usuarioEntradaDTO.getNombreUsuario()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        Usuario usuario = Usuario.builder()
                .nombreUsuario(usuarioEntradaDTO.getNombreUsuario())
                .pin(Integer.parseInt(passwordEncoder.encode(String.valueOf(usuarioEntradaDTO.getPin()))))
                .rol(usuarioEntradaDTO.getRol() != null ? usuarioEntradaDTO.getRol() : Role.ROLE_USER)
                .build();

        return usuarioRepository.save(usuario);
    }

    public UsuarioSalidaDTO obtenerUsuarioDTOPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return mapToDto(usuario);
    }


    public Usuario cambiarPin(String nombreUsuario, int nuevoPin) {
        Usuario usuario = usuarioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        usuario.setPin(Integer.parseInt(passwordEncoder.encode(String.valueOf(nuevoPin))));
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuarioPorIdDto(Long id, UsuarioEntradaDTO usuarioEntradaDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombreUsuario(usuarioEntradaDTO.getNombreUsuario());

        if (usuarioEntradaDTO.getPin() != 0) {
            usuario.setPin(Integer.parseInt(passwordEncoder.encode(String.valueOf(usuarioEntradaDTO.getPin()))));
        }

        if (securityService.isAdmin() && usuarioEntradaDTO.getRol() != null) {
            usuario.setRol(usuarioEntradaDTO.getRol());
        }

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
    public Usuario mapFromDto(UsuarioEntradaDTO usuarioEntradaDTO) {
        if (usuarioEntradaDTO == null) {
            return null;
        }
        return Usuario.builder()
                .nombreUsuario(usuarioEntradaDTO.getNombreUsuario())
                .pin(usuarioEntradaDTO.getPin())
                .build();
    }
    public Usuario crearUsuarioDto(UsuarioEntradaDTO usuarioEntradaDTO) {
        Usuario usuario = mapFromDto(usuarioEntradaDTO);
        return usuarioRepository.save(usuario);
    }

}
