package com.example.demo.controllers;

import com.example.demo.dto.TarjetaDebitoEntradaDTO;
import com.example.demo.dto.TarjetaDebitoSalidaDTO;
import com.example.demo.services.TarjetaDebitoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas/debito")
public class TarjetaDebitoController {

    @Autowired
    private TarjetaDebitoService service;

    @Operation(summary = "Crear una nueva tarjeta de débito")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TarjetaDebitoSalidaDTO> crear(@RequestBody TarjetaDebitoEntradaDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @Operation(summary = "Buscar tarjeta de débito por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @tarjetaDebitoServiceImpl.esDueño(#id)")
    public ResponseEntity<TarjetaDebitoSalidaDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todas las tarjetas de débito")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TarjetaDebitoSalidaDTO> listarTodas() {
        return service.listarTodas();
    }

    @Operation(summary = "Actualizar tarjeta de débito")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TarjetaDebitoSalidaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody TarjetaDebitoEntradaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar tarjeta de débito")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retirar dinero con tarjeta de débito")
    @PostMapping("/{id}/retirar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @tarjetaDebitoServiceImpl.esDueño(#id)")
    public ResponseEntity<Boolean> retirarDinero(@PathVariable Long id, @RequestParam double monto) {
        if (monto <= 0) {
            return ResponseEntity.badRequest().build();
        }
        boolean exito = service.retirarDinero(id, monto);
        return ResponseEntity.ok(exito);
    }

    @Operation(summary = "Pagar con tarjeta de débito")
    @PostMapping("/{id}/pagar-con")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @tarjetaDebitoServiceImpl.esDueño(#id)")
    public ResponseEntity<Boolean> pagarConTarjeta(@PathVariable Long id, @RequestParam double monto) {
        if (monto <= 0) {
            return ResponseEntity.badRequest().build();
        }
        boolean exito = service.pagarConTarjeta(id, monto);
        return ResponseEntity.ok(exito);
    }
}
