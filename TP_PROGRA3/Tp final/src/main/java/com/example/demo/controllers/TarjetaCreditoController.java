package com.example.demo.controllers;

import com.example.demo.dto.TarjetaCreditoDTO;
import com.example.demo.entities.TarjetaCredito;
import com.example.demo.services.TarjetaCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas/credito")
public class TarjetaCreditoController {

    @Autowired
    private TarjetaCreditoService service;

    @PostMapping
    public ResponseEntity<TarjetaCredito> crear(@RequestBody TarjetaCreditoDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaCredito> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TarjetaCredito> listarTodas() {
        return service.listarTodas();
    }

    @PutMapping
    public ResponseEntity<TarjetaCredito> actualizar(@RequestBody TarjetaCreditoDTO dto) {
        return ResponseEntity.ok(service.actualizar(dto));
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