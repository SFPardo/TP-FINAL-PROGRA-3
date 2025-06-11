package com.example.demo.services.impl;

import com.example.demo.config.CustomUser;
import com.example.demo.entities.Credencial;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Collection;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Credencial credencial = usuario.getCredencial();
        if (credencial == null) {
            throw new UsernameNotFoundException("Credencial not found for user: " + username);
        }

        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

        return new CustomUser(
                usuario.getUsuarioId(),
                usuario.getNombreUsuario(),
                credencial.getPin(),
                authorities
        );
    }
}