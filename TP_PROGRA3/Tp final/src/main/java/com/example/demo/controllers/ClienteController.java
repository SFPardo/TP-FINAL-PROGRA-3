package com.example.demo.controllers;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
import com.example.demo.dto.UsuarioSalidaDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.services.impl.ClienteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
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

    // -- METODOS POST -- //
    @Operation(summary = "Crear un nuevo cliente",
            description = "Crea un nuevo cliente con usuario y cuenta. Accesible por cualquier usuario.")
    @PostMapping("/crear")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ClienteSalidaDTO> crearCliente(@Valid @RequestBody ClienteEntradaDTO dto) {
        ClienteSalidaDTO cliente = clienteServiceImpl.crearClienteConUsuarioYCuenta(dto);
        return ResponseEntity.ok(cliente);
    }

    // -- METODOS GET -- //
    @Operation(summary = "Obtener todos los clientes",
            description = "Solo accesible por usuarios con rol ADMIN. Devuelve una lista de todos los clientes.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteSalidaDTO>> listarClientes() {
        return ResponseEntity.ok(clienteServiceImpl.obtenerTodosLosClientesDTO());
    }

    @Operation(summary = "Buscar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID).")
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorId(@PathVariable Long id) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorIdConDTO(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por DNI",
            description = "Accesible por ADMIN (cualquier DNI)")
    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorDni(@PathVariable String dni) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorDni(dni));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por email",
            description = "Accesible por ADMIN (cualquier email)")
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorEmail(@PathVariable String email) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorEmail(email));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por telefono",
            description = "Accesible por ADMIN (cualquier telefono)")
    @GetMapping("/telefono/{telefono}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorTelefono(@PathVariable String telefono) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorTelefono(telefono));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Obtener el usuario asociado al cliente autenticado",
            description = "Devuelve los detalles del usuario del cliente actualmente autenticado.")
    @GetMapping("/mi-usuario")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioActual(Long id) {
        UsuarioSalidaDTO usuario = clienteServiceImpl.obtenerUsuarioActual(id);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Ver el domicilio del cliente autenticado",
            description = "Devuelve los detalles del domicilio del cliente actualmente autenticado.")
    @GetMapping("/mi-domicilio")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<DomicilioEntradaSalidaDTO> verMiDomicilio(Long id) {
        DomicilioEntradaSalidaDTO domicilio = clienteServiceImpl.verMiDomicilio(id);
        return domicilio != null ? ResponseEntity.ok(domicilio) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Buscar cliente por Alias de cuenta",
            description = "Accesible por ADMIN (cualquier alias).")
    @GetMapping("/alias/{alias}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorAlias(@PathVariable String alias) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorAlias(alias);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por CBU",
            description = "Accesible por ADMIN (cualquier CBU).")
    @GetMapping("/cbu/{cbu}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorCbu(@PathVariable String cbu) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorCbu(cbu);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar clientes por provincia",
            description = "Accesible por ADMIN (cualquier provincia).")
    @GetMapping("/provincia/{provincia}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorProvincia(@PathVariable String provincia) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorProvincia(provincia);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar clientes por ciudad",
            description = "Accesible por ADMIN (cualquier ciudad).")
    @GetMapping("/ciudad/{ciudad}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorCiudad(ciudad);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -- METODOS DELETE -- //

    @Operation(summary = "Eliminar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID). Elimina un cliente por su ID.")
    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarClientePorId(@PathVariable Long id) {
        clienteServiceImpl.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar cliente por DNI",
            description = "Accesible por ADMIN (cualquier DNI). Elimina un cliente por su DNI.")
    @DeleteMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarClientePorDni(@PathVariable String dni) {
        clienteServiceImpl.eliminarClientePorDni(dni);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar cliente por email",
            description = "Accesible por ADMIN (cualquier email). Elimina un cliente por su email.")
    @DeleteMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarClientePorEmail(@PathVariable String email) {
        clienteServiceImpl.eliminarClientePorEmail(email);
        return ResponseEntity.noContent().build();
    }


    // -- METODOS PUT y PATCH -- //

    @Operation(summary = "Actualizar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID). Actualiza los datos de un cliente.")
    @PutMapping("/{id}/domicilio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarDomicilio(
            @PathVariable Long id,
            @RequestBody DomicilioEntradaSalidaDTO nuevoDomicilio) {
        return ResponseEntity.ok(clienteServiceImpl.actualizarDomicilio(id, nuevoDomicilio));
    }

    @Operation(summary = "Actualizar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID). Actualiza los datos de un cliente.")
    @PatchMapping("/{id}/nombre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarNombre(
            @PathVariable Long id,
            @RequestParam String nuevoNombre) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarNombre(id, nuevoNombre);
        return ResponseEntity.ok(clienteActualizado);
    }

    @Operation(summary = "Actualizar email por ID",
            description = "Accesible por ADMIN (cualquier ID). Actualiza los datos de un cliente.")
    @PatchMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarEmail(
            @PathVariable Long id,
            @RequestParam String nuevoEmail) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarEmail(id, nuevoEmail);
        return ResponseEntity.ok(clienteActualizado);
    }

    @Operation(summary = "Actualizar telefono por ID",
            description = "Accesible por ADMIN (cualquier ID). Actualiza los datos de un cliente.")
    @PatchMapping("/{id}/telefono")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarTelefono(
            @PathVariable Long id,
            @RequestBody String nuevoTelefono) {
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarTelefono(id, nuevoTelefono);
        return ResponseEntity.ok(clienteActualizado);
    }





}
