package com.example.demo.services;

import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
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
    ClienteSalidaDTO crearClienteConUsuarioYCuenta(ClienteEntradaDTO dto);
    ClienteSalidaDTO actualizarDomicilio(Long id, DomicilioEntradaSalidaDTO nuevoDomicilio);
}
