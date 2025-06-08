package com.example.demo.controllers;

import com.example.demo.dto.TarjetaDebitoEntradaDTO;
import com.example.demo.dto.TarjetaDebitoSalidaDTO;
import com.example.demo.services.TarjetaDebitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas/debito")
public class TarjetaDebitoController {

    @Autowired
    private TarjetaDebitoService service;

    @PostMapping
    public ResponseEntity<TarjetaDebitoSalidaDTO> crear(@RequestBody TarjetaDebitoEntradaDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaDebitoSalidaDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TarjetaDebitoSalidaDTO> listarTodas() {
        return service.listarTodas();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarjetaDebitoSalidaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody TarjetaDebitoEntradaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/retirar")
    public ResponseEntity<Boolean> retirarDinero(@PathVariable Long id, @RequestParam double monto) {
        boolean exito = service.retirarDinero(id, monto);
        return ResponseEntity.ok(exito);
    }

    @PostMapping("/{id}/pagar-con")
    public ResponseEntity<Boolean> pagarConTarjeta(@PathVariable Long id, @RequestParam double monto) {
        boolean exito = service.pagarConTarjeta(id, monto);
        return ResponseEntity.ok(exito);
    }

}
