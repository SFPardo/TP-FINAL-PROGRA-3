package com.example.demo.controllers;
import com.example.demo.entities.Cliente;
import com.example.demo.dto.ClienteDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.Domicilio;
import com.example.demo.services.ClienteService;

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

    private final ClienteService clienteService;

    @PostMapping("/crear")
    public ResponseEntity<Cliente> crearCliente(@RequestBody ClienteDTO dto) {
        Cliente cliente = clienteService.crearClienteConUsuarioYCuenta(dto);
        return ResponseEntity.ok(cliente);
    }
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.obtenerTodosLosClientes());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.buscarClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarClienteConDTO(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/dni/{dni}")
    public ResponseEntity<Void> eliminarClientePorDni(@PathVariable String dni) {
        clienteService.eliminarClientePorDni(dni);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cuentas")
    public ResponseEntity<List<Cuenta>> obtenerCuentas(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarClientePorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return ResponseEntity.ok(cliente.getUsuario().getCuentaList());
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Cliente> obtenerPorDni(@PathVariable String dni) {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        return cliente != null ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/domicilio")
    public ResponseEntity<Cliente> actualizarDomicilio(
            @PathVariable Long id,
            @RequestBody Domicilio nuevoDomicilio) {
        return ResponseEntity.ok(clienteService.actualizarDomicilio(id, nuevoDomicilio));
    }






}
