package com.example.demo.services.impl;

import com.example.demo.dto.CambiarPinDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.dto.UsuarioEntradaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Credencial;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.CredencialRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private CredencialRepository credencialRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // -- Metodos de obtener sin DTO --//
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    public Usuario buscarUsuarioPorUsername(String username) {
        return usuarioRepository.findByNombreUsuario(username).orElse(null);
    }
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // -- Metodos de crear sin DTO --//
    public void crearUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    // -- Metodos de actualizar sin DTO --//
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    // -- Metodos de eliminar sin DTO --//
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

    // -- Metodos con DTO --//
    public void actualizarUsuarioPorIdDto(Long id, UsuarioEntradaDTO usuarioEntradaDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setNombreUsuario(usuarioEntradaDTO.getNombreUsuario());
        Credencial credencial = Credencial.builder()
                .pin(usuarioEntradaDTO.getCredencial().getPin())
                .build();
        usuario.setCredencial(credencial);
        usuarioRepository.save(usuario);
    }
    public static UsuarioSalidaDTO mapToDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
    }
    public UsuarioSalidaDTO mapToSalidaDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioSalidaDTO(
                usuario.getUsuarioId(),
                usuario.getNombreUsuario()
        );
    }
    public List<UsuarioSalidaDTO> mapToDtoList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioServiceImpl::mapToDto)
                .toList();
    }
    public List<UsuarioSalidaDTO> obtenerTodosLosUsuariosDTO() {
        return mapToDtoList(usuarioRepository.findAll());
    }
    public Page<UsuarioSalidaDTO> obtenerTodosLosUsuariosPaginados(Pageable pageable) {
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);
        return usuariosPage.map(this::mapToSalidaDTO);
    }
    public Usuario mapFromDto(UsuarioEntradaDTO usuarioEntradaDTO) {
        if (usuarioEntradaDTO == null) {
            return null;
        }
        Credencial credencial = Credencial.builder()
                .pin(usuarioEntradaDTO.getCredencial().getPin())
                .build();

        return Usuario.builder()
                .nombreUsuario(usuarioEntradaDTO.getNombreUsuario())
                .credencial(credencial)
                .build();
    }
    public Usuario crearUsuarioDto(UsuarioEntradaDTO usuarioEntradaDTO) {
        Usuario usuario = mapFromDto(usuarioEntradaDTO);
        return usuarioRepository.save(usuario);
    }
    @Transactional // Para asegurar que la actualización de la credencial sea atómica
    public void cambiarPinUsuarioAutenticado(String nombreUsuario, CambiarPinDTO cambioPinDTO) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario autenticado no encontrado.");
        }

        Usuario usuario = usuarioOptional.get();
        Credencial credencial = usuario.getCredencial();

        if (credencial == null) {
            throw new RuntimeException("Credencial no encontrada para el usuario.");
        }
        if (!passwordEncoder.matches(cambioPinDTO.getPinActual(), credencial.getPin())) {
            throw new RuntimeException("El PIN actual es incorrecto.");
        }



        if (!cambioPinDTO.getNuevoPin().equals(cambioPinDTO.getConfirmarNuevoPin())) {
            throw new RuntimeException("El nuevo PIN y su confirmación no coinciden.");
        }

        String nuevoPinCifrado = passwordEncoder.encode(cambioPinDTO.getNuevoPin());
        credencial.setPin(nuevoPinCifrado);


        credencialRepository.save(credencial);
    }
    @Transactional
    public void cambiarPinDeUsuarioPorAdmin(Long usuarioId, String nuevoPin) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }

        Usuario usuario = usuarioOptional.get();
        Credencial credencial = usuario.getCredencial();

        if (credencial == null) {
            throw new RuntimeException("Credencial no encontrada para el usuario con ID: " + usuarioId);
        }

        String nuevoPinCifrado = passwordEncoder.encode(nuevoPin);
        credencial.setPin(nuevoPinCifrado);

        credencialRepository.save(credencial);
    }

}
