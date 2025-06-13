package com.example.demo.services.impl;

import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.*;
import com.example.demo.entities.enums.TipoCuenta;
import com.example.demo.entities.enums.TipoRol;
import com.example.demo.repositories.ClienteRepository;
import com.example.demo.repositories.CredencialRepository;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.ClienteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    private final CredencialRepository credencialRepository;
    @Autowired
    private GeneradorAliasServiceImpl generadorAliasServiceImpl;
    @Autowired
    private GeneradorCbuServiceImpl generadorCbuServiceImpl;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // -- Metodos para obtener sin DTO --//
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
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }
    @Override
    public boolean existeDni(String dni) {
        return clienteRepository.findByDni(dni).isPresent();
    }

    // -- Metodos para actualizar sin DTO --//
    @Override
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // -- Metodos para eliminar sin DTO --//
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


    //-- Metodos que implementan DTO --//
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
    public Page<ClienteSalidaDTO> obtenerTodosLosClientesPaginados(Pageable pageable) {
        Page<Cliente> clientesPage = clienteRepository.findAll(pageable);
        return clientesPage.map(this::mapToSalidaDTO);
    }
    @Override
    @Transactional
    public ClienteSalidaDTO crearClienteAdmin(ClienteEntradaDTO dto){
        if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese DNI.");
        }
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email.");
        }
        if (clienteRepository.findByTelefono(dto.getTelefono()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese teléfono.");
        }
        if (usuarioRepository.findByNombreUsuario(dto.getUsuario().getNombreUsuario()).isPresent()) {
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
                .rol(dto.getUsuario().getRol())
                .build();
        usuario = usuarioRepository.save(usuario);

        Credencial credencial = Credencial.builder()
                .usuario(usuario)
                .pin(passwordEncoder.encode(dto.getUsuario().getCredencial().getPin()))
                .build();
        credencialRepository.save(credencial);

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
        cuenta = cuentaRepository.save(cuenta);

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
    @Transactional
    public ClienteSalidaDTO crearClienteSinPermisos(ClienteEntradaDTO dto){
        // --- Validaciones de existencia ---
        if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese DNI.");
        }
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email.");
        }
        if (clienteRepository.findByTelefono(dto.getTelefono()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese teléfono.");
        }
        if (usuarioRepository.findByNombreUsuario(dto.getUsuario().getNombreUsuario()).isPresent()) {
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
                .rol(TipoRol.CLIENTE)
                .build();
        usuario = usuarioRepository.save(usuario);

        Credencial credencial = Credencial.builder()
                .usuario(usuario)
                .pin(passwordEncoder.encode(dto.getUsuario().getCredencial().getPin()))
                .build();
        credencialRepository.save(credencial);

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
        cuenta = cuentaRepository.save(cuenta);

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
    public ClienteSalidaDTO buscarClientePorIdConDTO(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return mapToSalidaDTO(cliente);
    }
    @Override
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
    @Override
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
    @Override
    public DomicilioEntradaSalidaDTO verMiDomicilio(String nombreUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (usuario.getCliente() != null) {
                Cliente cliente = usuario.getCliente();
                if (cliente.getDomicilio() != null) {
                    Domicilio domicilio = cliente.getDomicilio();
                    return new DomicilioEntradaSalidaDTO(
                            domicilio.getProvincia(),
                            domicilio.getCiudad(),
                            domicilio.getCalle(),
                            domicilio.getAltura()
                    );
                }
            }
        }
        return null;
    }
    @Override
    public ClienteSalidaDTO cambiarNombre(Long id, String nuevoNombre) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(nuevoNombre);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return mapToSalidaDTO(clienteActualizado);
    }
    @Override
    public ClienteSalidaDTO cambiarEmail(Long id, String nuevoEmail) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (clienteRepository.findByEmail(nuevoEmail).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email.");
        }

        cliente.setEmail(nuevoEmail);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return mapToSalidaDTO(clienteActualizado);
    }
    @Override
    public ClienteSalidaDTO cambiarTelefono(Long id, String nuevoTelefono) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (clienteRepository.findByTelefono(nuevoTelefono).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese teléfono.");
        }

        cliente.setTelefono(nuevoTelefono);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return mapToSalidaDTO(clienteActualizado);
    }
    @Override
    public ClienteSalidaDTO buscarClientePorAlias(String alias) {
        Cuenta cuenta = cuentaRepository.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el alias: " + alias));
        Cliente cliente = cuenta.getUsuario().getCliente();
        return mapToSalidaDTO(cliente);
    }
    @Override
    public ClienteSalidaDTO buscarClientePorCbu(String cbu) {
        Cuenta cuenta = cuentaRepository.findByCbu(cbu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el CBU: " + cbu));
        Cliente cliente = cuenta.getUsuario().getCliente();
        return mapToSalidaDTO(cliente);
    }
    @Override
    public List<ClienteSalidaDTO> buscarClientesPorProvincia(String provincia) {
        return clienteRepository.findByDomicilioProvincia(provincia).stream()
                .map(this::mapToSalidaDTO)
                .toList();
    }
    @Override
    public List<ClienteSalidaDTO> buscarClientesPorCiudad(String ciudad) {
        return clienteRepository.findByDomicilioCiudad(ciudad).stream()
                .map(this::mapToSalidaDTO)
                .toList();
    }
    @Override
    @Transactional
    public DomicilioEntradaSalidaDTO actualizarDomicilioClienteAutenticado(String nombreUsuario, DomicilioEntradaSalidaDTO dto) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOptional.isEmpty() || usuarioOptional.get().getCliente() == null) {
            throw new RuntimeException("Usuario no encontrado o no asociado a un cliente.");
        }

        Cliente cliente = usuarioOptional.get().getCliente();
        Domicilio domicilio = cliente.getDomicilio();

        if (domicilio == null) {
            throw new RuntimeException("El cliente autenticado no tiene un domicilio para actualizar.");
        }

        if (dto.getProvincia() != null && !dto.getProvincia().isEmpty()) {
            domicilio.setProvincia(dto.getProvincia());
        }
        if (dto.getCiudad() != null && !dto.getCiudad().isEmpty()) {
            domicilio.setCiudad(dto.getCiudad());
        }
        if (dto.getCalle() != null && !dto.getCalle().isEmpty()) {
            domicilio.setCalle(dto.getCalle());
        }
        if (dto.getAltura() > 0) {
            domicilio.setAltura(dto.getAltura());
        }

        clienteRepository.save(cliente);

        // Retornar el DTO del domicilio actualizado
        return new DomicilioEntradaSalidaDTO(
                domicilio.getProvincia(),
                domicilio.getCiudad(),
                domicilio.getCalle(),
                domicilio.getAltura()
        );
    }

    @Transactional
    public List<ClienteSalidaDTO> crearMultiplesClientesAdmin(List<ClienteEntradaDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("La lista de clientes a crear no puede ser nula o vacía.");
        }
        List<ClienteSalidaDTO> clientesCreados = new ArrayList<>();
        for (ClienteEntradaDTO dto : dtos) {
            clientesCreados.add(crearClienteAdmin(dto));
        }
        return clientesCreados;
    }

}
