package com.example.demo.services;

import com.example.demo.entities.Usuario;

import java.util.List;

public interface UsuarioService {
    void crearUsuario(Usuario usuario);
    Usuario buscarUsuarioPorId(Long id);
    Usuario buscarUsuarioPorUsername(String username);
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);
    void eliminarUsuarioPorUsername(String username);
    void eliminarUsuarioPorId(Long id);
    List<Usuario> obtenerTodosLosUsuarios();
}
