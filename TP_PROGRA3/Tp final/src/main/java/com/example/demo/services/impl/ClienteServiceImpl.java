package com.example.demo.services.impl;

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
import com.example.demo.services.ClienteService;
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
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private final ClienteRepository clienteRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final CuentaRepository cuentaRepository;
    @Autowired
    private GeneradorAliasServiceImpl generadorAliasServiceImpl;
    @Autowired
    private GeneradorCbuServiceImpl generadorCbuServiceImpl;
    @Autowired
    private  UsuarioServiceImpl usuarioServiceImpl;

    @Override
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    @Override
    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }
    @Override
    public Cliente buscarClientePorDni(String dni) {
        return clienteRepository.findByDni(dni).orElse(null);
    }
    @Override
    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }
    @Override
    public Cliente buscarClientePorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }
    @Override
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    @Override
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
    @Override
    public void eliminarClientePorDni(String dni) {
        Cliente cliente = buscarClientePorDni(dni);
        if (cliente != null) {
            clienteRepository.delete(cliente);
        }
    }
    @Override
    public void eliminarClientePorEmail(String email) {
        Cliente cliente = buscarClientePorEmail(email);
        if (cliente != null) {
            clienteRepository.delete(cliente);
        }
    }
    @Override
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }
    @Override
    public ClienteSalidaDTO mapToSalidaDTO(Cliente cliente) {
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
    @Override
    public List<ClienteSalidaDTO> obtenerTodosLosClientesDTO() {
        return clienteRepository.findAll().stream()
                .map(this::mapToSalidaDTO)
                .toList();
    }

    @Override
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

        String alias = generadorAliasServiceImpl.generarAlias();
        String cbu = generadorCbuServiceImpl.generarCbu();

        Cuenta cuenta = Cuenta.builder()
                .alias(alias)
                .cbu(cbu)
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

    public boolean existeDni(String dni) {
        return clienteRepository.findByDni(dni).isPresent();
    }


    public UsuarioSalidaDTO obtenerUsuarioActual(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario usuario = cliente.getUsuario();
        if (usuario != null) {
            UsuarioSalidaDTO usuarioDto = UsuarioServiceImpl.mapToDto(usuario);
            return usuarioDto;
        }
        return null;

    }

    public DomicilioEntradaSalidaDTO verMiDomicilio(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Domicilio domicilio = cliente.getDomicilio();
        if (domicilio != null) {
            DomicilioEntradaSalidaDTO domicilioDto = new DomicilioEntradaSalidaDTO();
            domicilioDto.setProvincia(domicilio.getProvincia());
            domicilioDto.setCiudad(domicilio.getCiudad());
            domicilioDto.setCalle(domicilio.getCalle());
            domicilioDto.setAltura(domicilio.getAltura());
            return domicilioDto;
        }
        return null;

    }
}
