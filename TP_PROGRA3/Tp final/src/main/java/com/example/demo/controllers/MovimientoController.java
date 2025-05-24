package com.example.demo.controllers;

import com.example.demo.entities.Movimiento;
import com.example.demo.controllers.MovimientoController;

import com.example.demo.services.MovimientoServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/movimiento")

@RequiredArgsConstructor
public class MovimientoController {

    private MovimientoServices movimientoServices;

    @PostMapping
    public ResponseEntity<Movimiento> crearMovimiento(@Valid @RequestBody Movimiento movimiento){
        Movimiento nuevoMovimiento = movimientoServices.crearMovimiento(movimiento);
        return ResponseEntity.ok(nuevoMovimiento);
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarMovimientos(@Valid @RequestBody Movimiento movimiento){
        return ResponseEntity.ok(movimientoServices.listarMovimientos());
    }

    @GetMapping("{id}")
    public ResponseEntity<Movimiento> buscarMovimientoPorId(@RequestParam int id) {
        return movimientoServices.buscarMovimientoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        if (movimientoServices.buscarMovimientoPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        movimientoServices.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
