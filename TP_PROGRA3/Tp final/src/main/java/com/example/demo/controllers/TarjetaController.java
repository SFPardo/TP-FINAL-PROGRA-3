package com.example.demo.controllers;

import com.example.demo.entities.Tarjeta;
import com.example.demo.services.TarjetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas")
@RequiredArgsConstructor

public class TarjetaController {

    private final TarjetaService tarjetaService;


    @PostMapping
    public ResponseEntity<Tarjeta> crearTarjeta(@Valid @RequestBody Tarjeta tarjeta) {
        Tarjeta nueva = tarjetaService.crearTarjeta(tarjeta);
        return ResponseEntity.ok(nueva);
    }
    @GetMapping
    public ResponseEntity<List<Tarjeta>> listarTarjetas() {
        return ResponseEntity.ok(tarjetaService.listarTodas());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Tarjeta> buscarTarjetaPorId(@PathVariable Long id) {
        return tarjetaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<Tarjeta> buscarTarjetaPorNumero(@RequestParam String numero) {
        return tarjetaService.buscarPorNumero(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarjeta> actualizarTarjeta(@PathVariable Long id, @Valid @RequestBody Tarjeta tarjeta) {
        return tarjetaService.buscarPorId(id)
                .map (t -> {
                    tarjeta.setTarjetaId(id);
                    return ResponseEntity.ok(tarjetaService.actualizarTarjeta(tarjeta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable Long id) {
        if (tarjetaService.buscarPorId(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        tarjetaService.eliminarTarjeta(id);
        return ResponseEntity.noContent().build();
    }


}
