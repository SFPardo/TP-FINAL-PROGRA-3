package com.example.demo.services;

import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    Cliente crearCliente(Cliente cliente);
    Optional<Cliente> buscarClientePorId(Long id);
    Cliente buscarClientePorDni(String dni);
    Cliente buscarClientePorEmail(String email);
    Cliente buscarClientePorTelefono(String telefono);
    Cliente actualizarCliente(Cliente cliente);
    void eliminarCliente(Long id);
    void eliminarClientePorDni(String dni);
    void eliminarClientePorEmail(String email);
    List<Cliente> obtenerTodosLosClientes();
    ClienteSalidaDTO mapToSalidaDTO(Cliente cliente);
    List<ClienteSalidaDTO> obtenerTodosLosClientesDTO();
    ClienteSalidaDTO crearClienteAdmin(ClienteEntradaDTO dto);
    ClienteSalidaDTO actualizarDomicilio(Long id, DomicilioEntradaSalidaDTO nuevoDomicilio);
    boolean existeDni(String dni);
    ClienteSalidaDTO actualizarClienteConDTO(Long id, ClienteEntradaDTO dto);
    public DomicilioEntradaSalidaDTO verMiDomicilio(String username);
    public ClienteSalidaDTO cambiarNombre(Long id, String nuevoNombre);
    public ClienteSalidaDTO cambiarEmail(Long id, String nuevoEmail);
    public ClienteSalidaDTO cambiarTelefono(Long id, String nuevoTelefono);
    public ClienteSalidaDTO buscarClientePorAlias(String alias);
    public ClienteSalidaDTO buscarClientePorCbu(String cbu);
    public List<ClienteSalidaDTO> buscarClientesPorProvincia(String provincia);
    public List<ClienteSalidaDTO> buscarClientesPorCiudad(String ciudad);
    public DomicilioEntradaSalidaDTO actualizarDomicilioClienteAutenticado(String nombreUsuario, DomicilioEntradaSalidaDTO dto);
}
