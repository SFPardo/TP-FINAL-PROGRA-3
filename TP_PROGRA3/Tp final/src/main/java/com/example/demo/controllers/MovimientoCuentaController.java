package com.example.demo.controllers;

import com.example.demo.dto.MovimientoCuentaEntradaDTO;
import com.example.demo.dto.MovimientoCuentaSalidaDTO;
import com.example.demo.entities.Movimiento;

import com.example.demo.services.impl.MovimientoCuentaServicesImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/movimientoCuenta")
public class MovimientoCuentaController {

    private MovimientoCuentaServicesImpl movimientoCuentaServicesImpl;

    @PostMapping
    public ResponseEntity<MovimientoCuentaEntradaDTO> crearMovimientoCuenta(@Valid @RequestBody MovimientoCuentaEntradaDTO movimiento){
        movimientoCuentaServicesImpl.crearMovimiento(movimiento);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoCuentaSalidaDTO>> listarMovimientos(){
        return ResponseEntity.ok(movimientoCuentaServicesImpl.listarMovimientos());
    }

    @GetMapping("{id}")
    public ResponseEntity<MovimientoCuentaSalidaDTO> buscarMovimientoPorId(@RequestParam Long id) {
        return ResponseEntity.ok(movimientoCuentaServicesImpl.buscarMovimientoPorId(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoCuentaServicesImpl.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
