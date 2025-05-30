package com.example.demo.controllers;

import com.example.demo.entities.Movimiento;

import com.example.demo.services.impl.MovimientoCuentaServicesImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/movimiento")

@RequiredArgsConstructor
public class MovimientoController {

    private MovimientoCuentaServicesImpl movimientoCuentaServicesImpl;

    @PostMapping
    public ResponseEntity<Movimiento> crearMovimiento(@Valid @RequestBody Movimiento movimiento){
        Movimiento nuevoMovimiento = movimientoCuentaServicesImpl.crearMovimiento(movimiento);
        return ResponseEntity.ok(nuevoMovimiento);
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarMovimientos(@Valid @RequestBody Movimiento movimiento){
        return ResponseEntity.ok(movimientoCuentaServicesImpl.listarMovimientos());
    }

    @GetMapping("{id}")
    public ResponseEntity<Movimiento> buscarMovimientoPorId(@RequestParam Long id) {
        return movimientoCuentaServicesImpl.buscarMovimientoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        if (movimientoCuentaServicesImpl.buscarMovimientoPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        movimientoCuentaServicesImpl.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
