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
import org.springframework.security.access.prepost.PreAuthorize;
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
    private ClienteServiceImpl clienteServiceImpl;

    @PostMapping("/crear")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ClienteSalidaDTO> crearCliente(@Valid @RequestBody ClienteEntradaDTO dto) {
        ClienteSalidaDTO cliente = clienteServiceImpl.crearClienteConUsuarioYCuenta(dto);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    public ResponseEntity<List<ClienteSalidaDTO>> listarClientes() {
        return ResponseEntity.ok(clienteServiceImpl.obtenerTodosLosClientesDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteSalidaDTO> buscarPorId(@PathVariable Long id) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorIdConDTO(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{dni}")
    public ResponseEntity<ClienteSalidaDTO> buscarPorDni(@PathVariable String dni) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorDni(dni));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{email}")
    public ResponseEntity<ClienteSalidaDTO> buscarPorEmail(@PathVariable String email) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorEmail(email));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{telefono}")
    public ResponseEntity<ClienteSalidaDTO> buscarPorTelefono(@PathVariable String telefono) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorTelefono(telefono));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarClientePorId(@PathVariable Long id) {
        clienteServiceImpl.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminarClientePorDni(@PathVariable String dni) {
        clienteServiceImpl.eliminarClientePorDni(dni);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{email}")
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

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<ClienteSalidaDTO> actualizarNombre(
            @PathVariable Long id,
            @RequestParam String nuevoNombre) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarNombre(id, nuevoNombre);
        return ResponseEntity.ok(clienteActualizado);
    }
    @PatchMapping("/{id}/email")
    public ResponseEntity<ClienteSalidaDTO> actualizarEmail(
            @PathVariable Long id,
            @RequestParam String nuevoEmail) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarEmail(id, nuevoEmail);
        return ResponseEntity.ok(clienteActualizado);
    }
    @PatchMapping("/{id}/telefono")
    public ResponseEntity<ClienteSalidaDTO> actualizarTelefono(
            @PathVariable Long id,
            @RequestBody String nuevoTelefono) {
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
    @GetMapping("/{provincia}")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorProvincia(@PathVariable String provincia) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorProvincia(provincia);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{ciudad}")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorCiudad(ciudad);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }




}
