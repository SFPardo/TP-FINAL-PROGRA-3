package com.example.demo.controllers;

import com.example.demo.dto.TarjetaCreditoEntradaDTO;
import com.example.demo.dto.TarjetaCreditoSalidaDTO;
import com.example.demo.services.TarjetaCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas/credito")
@RequiredArgsConstructor
public class TarjetaCreditoController {

    private final TarjetaCreditoService service;

    @PostMapping
    public ResponseEntity<TarjetaCreditoSalidaDTO> crear(@RequestBody TarjetaCreditoEntradaDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaCreditoSalidaDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TarjetaCreditoSalidaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarjetaCreditoSalidaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody TarjetaCreditoEntradaDTO dto
    ) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Void> pagarTarjeta(@PathVariable Long id, @RequestParam double monto) {
        service.pagarTarjeta(id, monto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/pagar-con")
    public ResponseEntity<Boolean> pagarConTarjeta(@PathVariable Long id, @RequestParam double monto) {
        boolean exito = service.pagarConTarjeta(id, monto);
        return ResponseEntity.ok(exito);
    }
}
