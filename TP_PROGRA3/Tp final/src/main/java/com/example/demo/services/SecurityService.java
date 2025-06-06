package com.example.demo.services;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UsuarioRepository usuarioRepository;

    public boolean puedeAccederAlUsuario(Authentication authentication, Long usuarioId) {
        String nombreUsuario = authentication.getName();
        Usuario usuarioActual = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // ADMIN puede acceder a cualquier usuario
        if (usuarioActual.getRol() == Role.ROLE_ADMIN) {
            return true;
        }

        // USER solo puede acceder a su propio usuario
        return usuarioActual.getUsuarioId().equals(usuarioId);
    }

    public boolean puedeAccederAlUsuarioPorUsername(Authentication authentication, String username) {
        String nombreUsuarioActual = authentication.getName();

        // ADMIN puede acceder a cualquier usuario
        if (usuarioRepository.findByNombreUsuario(nombreUsuarioActual)
                .orElseThrow().getRol() == Role.ROLE_ADMIN) {
            return true;
        }

        // USER solo puede acceder a su propio usuario
        return nombreUsuarioActual.equals(username);
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }


}
