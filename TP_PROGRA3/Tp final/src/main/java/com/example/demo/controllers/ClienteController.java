package com.example.demo.controllers;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.services.impl.ClienteServiceImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    @Autowired
    private final ClienteServiceImpl clienteServiceImpl;

    @PostMapping("/crear")
    public ResponseEntity<ClienteSalidaDTO> crearCliente(@Valid @RequestBody ClienteEntradaDTO dto) {
        ClienteSalidaDTO cliente = clienteServiceImpl.crearClienteConUsuarioYCuenta(dto);
        return ResponseEntity.ok(cliente);
    }
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        return ResponseEntity.ok(clienteServiceImpl.obtenerTodosLosClientes());
    }

    @GetMapping("/dto")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerTodosDTO() {
        return ResponseEntity.ok(clienteServiceImpl.obtenerTodosLosClientesDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteServiceImpl.buscarClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{idDto}")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorIdConDTO(@PathVariable Long id) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorIdConDTO(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorDni(@PathVariable String dni) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorDni(dni));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorEmail(@PathVariable String email) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorEmail(email));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/telefono/{telefono}")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorTelefono(@PathVariable String telefono) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorTelefono(telefono));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ClienteSalidaDTO> actualizarCliente(
            @PathVariable Long id,
            @RequestBody ClienteEntradaDTO dto) {
        return ResponseEntity.ok(clienteServiceImpl.actualizarClienteConDTO(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteServiceImpl.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/dni/{dni}")
    public ResponseEntity<Void> eliminarClientePorDni(@PathVariable String dni) {
        clienteServiceImpl.eliminarClientePorDni(dni);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> eliminarClientePorEmail(@PathVariable String email) {
        clienteServiceImpl.eliminarClientePorEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/domicilio")
    public ResponseEntity<ClienteSalidaDTO> actualizarDomicilio(
            @PathVariable Long id,
            @RequestBody DomicilioEntradaSalidaDTO nuevoDomicilio) {
        return ResponseEntity.ok(clienteServiceImpl.actualizarDomicilio(id, nuevoDomicilio));
    }

    @GetMapping("/mi-usuario")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioActual(Long id) {
        UsuarioSalidaDTO usuario = clienteServiceImpl.obtenerUsuarioActual(id);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @GetMapping("/mi-domicilio")
    public ResponseEntity<DomicilioEntradaSalidaDTO> verMiDomicilio(Long id) {
        DomicilioEntradaSalidaDTO domicilio = clienteServiceImpl.verMiDomicilio(id);
        return domicilio != null ? ResponseEntity.ok(domicilio) : ResponseEntity.notFound().build();
    }

    @PutMapping("/cambiarNombre/{id}")
    public ResponseEntity<ClienteSalidaDTO> cambiarNombre(
            @PathVariable Long id,
            @RequestParam String nuevoNombre) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarNombre(id, nuevoNombre);
        return ResponseEntity.ok(clienteActualizado);
    }
    @PutMapping("/cambiarEmail/{id}")
    public ResponseEntity<ClienteSalidaDTO> cambiarEmail(
            @PathVariable Long id,
            @RequestParam String nuevoEmail) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarEmail(id, nuevoEmail);
        return ResponseEntity.ok(clienteActualizado);
    }
    @PutMapping("/cambiarTelefono/{id}")
    public ResponseEntity<ClienteSalidaDTO> cambiarTelefono(
            @PathVariable Long id,
            @RequestParam String nuevoTelefono) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarTelefono(id, nuevoTelefono);
        return ResponseEntity.ok(clienteActualizado);
    }
    @GetMapping("/{alias}")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorAlias(@PathVariable String alias) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorAlias(alias);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{cbu}")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorCbu(@PathVariable String cbu) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorCbu(cbu);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/provincia/{provincia}")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorProvincia(@PathVariable String provincia) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorProvincia(provincia);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorCiudad(ciudad);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }




}
