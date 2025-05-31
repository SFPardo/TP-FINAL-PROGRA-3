package com.example.demo.services;

import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
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
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
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

    private ClienteSalidaDTO mapToSalidaDTO(Cliente cliente) {
        DomicilioEntradaSalidaDTO domicilioDTO = new DomicilioEntradaSalidaDTO();
        domicilioDTO.setProvincia(cliente.getDomicilio().getProvincia());
        domicilioDTO.setCiudad(cliente.getDomicilio().getCiudad());
        domicilioDTO.setCalle(cliente.getDomicilio().getCalle());
        domicilioDTO.setAltura(cliente.getDomicilio().getAltura());

        UsuarioSalidaDTO usuarioDTO = new UsuarioSalidaDTO();
        usuarioDTO.setUsuarioId(cliente.getUsuario().getUsuarioId());
        usuarioDTO.setNombreUsuario(cliente.getUsuario().getNombreUsuario());

        ClienteSalidaDTO salida = new ClienteSalidaDTO();
        salida.setClienteId(cliente.getClienteId());
        salida.setNombre(cliente.getNombre());
        salida.setDni(cliente.getDni());
        salida.setEmail(cliente.getEmail());
        salida.setTelefono(cliente.getTelefono());
        salida.setDomicilio(domicilioDTO);
        salida.setUsuario(usuarioDTO);

        return salida;
    }

    public List<ClienteSalidaDTO> obtenerTodosLosClientesDTO() {
        return clienteRepository.findAll().stream()
                .map(this::mapToSalidaDTO)
                .toList();
    }


    public ClienteSalidaDTO crearClienteConUsuarioYCuenta(ClienteEntradaDTO dto){
        if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese DNI.");
        }
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email.");
        }
        if (clienteRepository.findByTelefono(dto.getTelefono()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese teléfono.");
        }
        if (usuarioRepository.findByUsername(dto.getUsuario().getNombreUsuario()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese nombre de usuario.");
        }
        if (dto.getDomicilio() == null ||
                dto.getDomicilio().getProvincia() == null ||
                dto.getDomicilio().getCiudad() == null ||
                dto.getDomicilio().getCalle() == null ||
                dto.getDomicilio().getAltura() <= 0) {
            throw new RuntimeException("Los datos del domicilio están incompletos.");
        }

        Usuario usuario = Usuario.builder()
                .nombreUsuario(dto.getUsuario().getNombreUsuario())
                .pin(dto.getUsuario().getPin())
                .build();

        Cuenta cuenta = Cuenta.builder()
                .alias(generarAliasUnico())
                .cbu(generarCBUUnico())
                .saldo(BigDecimal.ZERO)
                .tipoCuenta(TipoCuenta.AHORRO_PESOS)
                .limiteSobregiro(BigDecimal.ZERO)
                .usuario(usuario)
                .build();

        usuario.setCuentaList(List.of(cuenta));

        Domicilio domicilio = Domicilio.builder()
                .provincia(dto.getDomicilio().getProvincia())
                .ciudad(dto.getDomicilio().getCiudad())
                .calle(dto.getDomicilio().getCalle())
                .altura(dto.getDomicilio().getAltura())
                .build();

        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .domicilio(domicilio)
                .usuario(usuario)
                .build();

        usuario.setCliente(cliente);

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return mapToSalidaDTO(clienteGuardado);
    }
    //ESTAS FUNCIONES SON PROVISIONALES PARA GENERAR CBU Y ALIAS ÚNICOS
    //CAMBIAR CUANDO GENERADORALIASSERVICE Y GENERADORCBUSERVICE ESTÉN IMPLEMENTADOS
    private String generarCBUUnico() {
        return UUID.randomUUID().toString().substring(0, 22);
    }
    private String generarAliasUnico() {
        return "alias" + new Random().nextInt(100000);
    }

    public ClienteSalidaDTO actualizarClienteConDTO(Long id, ClienteEntradaDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(dto.getNombre());
        cliente.setDni(dto.getDni());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());

        cliente.setDomicilio(Domicilio.builder()
                .provincia(dto.getDomicilio().getProvincia())
                .ciudad(dto.getDomicilio().getCiudad())
                .calle(dto.getDomicilio().getCalle())
                .altura(dto.getDomicilio().getAltura())
                .build());

        return mapToSalidaDTO(clienteRepository.save(cliente));
    }


    public ClienteSalidaDTO actualizarDomicilio(Long id, DomicilioEntradaSalidaDTO nuevoDomicilio) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setDomicilio(Domicilio.builder()
                .provincia(nuevoDomicilio.getProvincia())
                .ciudad(nuevoDomicilio.getCiudad())
                .calle(nuevoDomicilio.getCalle())
                .altura(nuevoDomicilio.getAltura())
                .build());

        return mapToSalidaDTO(clienteRepository.save(cliente));
    }


}
