package com.example.demo.services;

import com.example.demo.entities.Usuario;

import java.util.List;

public interface UsuarioService {
    void crearUsuario(Usuario usuario);
    Usuario buscarUsuarioPorId(Long id);
    Usuario buscarUsuarioPorUsername(String username);
    Usuario buscarUsuarioPorEmail(String email);
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);
    void eliminarUsuarioPorUsername(String username);
    void eliminarUsuarioPorEmail(String email);
    void eliminarUsuarioPorId(Long id);
    Usuario login(String nombreUsuario, int pin);
    Usuario cambiarPin(String nombreUsuario, int nuevoPin);
    List<Usuario> obtenerTodosLosUsuarios();
}
