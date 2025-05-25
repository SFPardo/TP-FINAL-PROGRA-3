package com.example.demo.services;

import com.example.demo.entities.Cliente;
import com.example.demo.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }
    public Cliente buscarClientePorDni(String dni) {
        return clienteRepository.findByDni(dni).orElse(null);
    }
    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }
    public Cliente buscarClientePorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
    public void eliminarClientePorDni(String dni) {
        Cliente cliente = buscarClientePorDni(dni);
        if (cliente != null) {
            clienteRepository.delete(cliente);
        }
    }
    public void eliminarClientePorEmail(String email) {
        Cliente cliente = buscarClientePorEmail(email);
        if (cliente != null) {
            clienteRepository.delete(cliente);
        }
    }
}
