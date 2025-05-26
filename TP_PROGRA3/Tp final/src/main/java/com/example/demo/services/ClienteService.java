package com.example.demo.services;

import com.example.demo.dto.ClienteDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.Domicilio;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.enums.TipoCuenta;
import com.example.demo.repositories.ClienteRepository;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {
    @Autowired
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
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

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente crearClienteConUsuarioYCuenta(ClienteDTO dto){
        Usuario usuario = Usuario.builder()
                .nombreUsuario(dto.getNombreUsuario())
                .pin(dto.getPin())
                .build();

        Cuenta cuenta = Cuenta.builder()
                .alias(generarAliasUnico()) // Método que podés implementar
                .cbu(generarCBUUnico())     // Método que podés implementar
                .saldo(BigDecimal.ZERO)
                .tipoCuenta(TipoCuenta.AHORRO_PESOS)
                .limiteSobregiro(BigDecimal.ZERO)
                .usuario(usuario) // Asociar el usuario a la cuenta
                .build();

        usuario.setCuentaList(List.of(cuenta));

        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .domicilio(dto.getDomicilio())
                .usuario(usuario) // Asociar el usuario al cliente
                .build();

        usuario.setCliente(cliente);

        return clienteRepository.save(cliente);

    }
    //ESTAS FUNCIONES SON PROVISIONALES PARA GENERAR CBU Y ALIAS ÚNICOS
    //CAMBIAR CUANDO GENERADORALIASSERVICE Y GENERADORCBUSERVICE ESTÉN IMPLEMENTADOS
    private String generarCBUUnico() {
        return UUID.randomUUID().toString().substring(0, 22);
    }
    private String generarAliasUnico() {
        return "alias" + new Random().nextInt(100000);
    }

    public Cliente actualizarClienteConDTO(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDni(dto.getDni());
        cliente.setDomicilio(dto.getDomicilio());

        return clienteRepository.save(cliente);
    }
    public Cliente actualizarDomicilio(Long id, Domicilio nuevoDomicilio) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setDomicilio(nuevoDomicilio);
        return clienteRepository.save(cliente);
    }
}
