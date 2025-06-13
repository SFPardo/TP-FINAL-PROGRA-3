package com.example.demo.controllers;

import com.example.demo.dto.TarjetaCreditoEntradaDTO;
import com.example.demo.dto.TarjetaCreditoSalidaDTO;
import com.example.demo.services.TarjetaCreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

@RestController
@RequestMapping("/api/tarjetas/credito")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Tarjetas de Crédito", description = "Operaciones relacionadas con tarjetas de crédito")
@RequiredArgsConstructor
public class TarjetaCreditoController {

    private final TarjetaCreditoService tarjetaCreditoService;

    @Operation(summary = "Obtener tarjeta de crédito por ID")
    @ApiResponse(responseCode = "200", description = "Tarjeta encontrada")
    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    @PreAuthorize("hasRole('ADMIN') or @tarjetaCreditoServiceImpl.esDueño(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<TarjetaCreditoSalidaDTO> obtenerPorId(@PathVariable Long id) {
        return tarjetaCreditoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todas las tarjetas de crédito")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TarjetaCreditoSalidaDTO>> listarTodas() {
        return ResponseEntity.ok(tarjetaCreditoService.listarTodas());
    }

    @Operation(summary = "Crear nueva tarjeta de crédito")
    @ApiResponse(responseCode = "201", description = "Tarjeta creada")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TarjetaCreditoSalidaDTO> crear(@RequestBody @Valid TarjetaCreditoEntradaDTO dto) {
        TarjetaCreditoSalidaDTO tarjeta = tarjetaCreditoService.crear(dto);
        return new ResponseEntity<>(tarjeta, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar tarjeta de crédito")
    @ApiResponse(responseCode = "200", description = "Tarjeta actualizada correctamente")
    @PreAuthorize("hasRole('ADMIN') or @tarjetaCreditoServiceImpl.esDueño(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<TarjetaCreditoSalidaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid TarjetaCreditoEntradaDTO dto) {

        TarjetaCreditoSalidaDTO tarjetaActualizada = tarjetaCreditoService.actualizar(id, dto);
        return ResponseEntity.ok(tarjetaActualizada);
    }

    @Operation(summary = "Eliminar tarjeta de crédito")
    @ApiResponse(responseCode = "204", description = "Tarjeta eliminada correctamente")
    @PreAuthorize("hasRole('ADMIN') or @tarjetaCreditoServiceImpl.esDueño(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tarjetaCreditoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pagar tarjeta de crédito")
    @ApiResponse(responseCode = "200", description = "Pago realizado correctamente")
    @PreAuthorize("hasRole('ADMIN') or @tarjetaCreditoServiceImpl.esDueño(#idTarjeta)")
    @PostMapping("/{idTarjeta}/pagar")
    public ResponseEntity<String> pagarTarjeta(@PathVariable Long idTarjeta, @RequestParam double monto) {
        if (monto <= 0) {
            return ResponseEntity.badRequest().body("El monto debe ser mayor a cero.");
        }

        tarjetaCreditoService.pagarTarjeta(idTarjeta, monto);
        return ResponseEntity.ok("Pago realizado con éxito");
    }

    @Operation(summary = "Pagar con tarjeta de crédito")
    @ApiResponse(responseCode = "200", description = "Pago con tarjeta realizado correctamente")
    @PreAuthorize("hasRole('ADMIN') or @tarjetaCreditoServiceImpl.esDueño(#idTarjeta)")
    @PostMapping("/{idTarjeta}/pagar-con-tarjeta")
    public ResponseEntity<String> pagarConTarjeta(@PathVariable Long idTarjeta, @RequestParam double monto) {
        if (monto <= 0) {
            return ResponseEntity.badRequest().body("El monto debe ser mayor a cero.");
        }

        tarjetaCreditoService.pagarConTarjeta(idTarjeta, monto);
        return ResponseEntity.ok("Compra con tarjeta realizada con éxito");
    }

    @Operation(summary = "Listar tarjetas de crédito paginadas")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/paginado")
    public Page<TarjetaCreditoSalidaDTO> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tarjetaCreditoService.listarPaginado(pageable);
    }
}
