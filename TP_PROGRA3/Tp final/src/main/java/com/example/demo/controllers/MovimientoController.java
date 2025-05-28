package com.example.demo.controllers;

import com.example.demo.entities.Movimiento;

import com.example.demo.services.impl.MovimientoServicesImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/movimiento")

@RequiredArgsConstructor
public class MovimientoController {

    private MovimientoServicesImpl movimientoServicesImpl;

    @PostMapping
    public ResponseEntity<Movimiento> crearMovimiento(@Valid @RequestBody Movimiento movimiento){
        Movimiento nuevoMovimiento = movimientoServicesImpl.crearMovimiento(movimiento);
        return ResponseEntity.ok(nuevoMovimiento);
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarMovimientos(@Valid @RequestBody Movimiento movimiento){
        return ResponseEntity.ok(movimientoServicesImpl.listarMovimientos());
    }

    @GetMapping("{id}")
    public ResponseEntity<Movimiento> buscarMovimientoPorId(@RequestParam Long id) {
        return movimientoServicesImpl.buscarMovimientoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        if (movimientoServicesImpl.buscarMovimientoPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        movimientoServicesImpl.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
