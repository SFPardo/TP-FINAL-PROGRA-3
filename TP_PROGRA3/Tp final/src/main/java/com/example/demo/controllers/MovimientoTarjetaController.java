package com.example.demo.controllers;

import com.example.demo.dto.MovimientoTarjetaEntradaDTO;
import com.example.demo.dto.MovimientoTarjetaSalidaDTO;
import com.example.demo.services.impl.MovimientoTarjetaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/movimientoTarjeta")
public class MovimientoTarjetaController {
    @Autowired
    private MovimientoTarjetaServiceImpl movimientoTarjetaServicesImpl;

    @PostMapping("/crearCredito")
    public ResponseEntity<MovimientoTarjetaEntradaDTO> crearMovimientoTarjetaCredito(@Valid @RequestBody MovimientoTarjetaEntradaDTO dto) {
        movimientoTarjetaServicesImpl.crearMovimientoCredito(dto);
        return ResponseEntity.ok(dto);
    }
    @PostMapping("/crearDebito")
    public ResponseEntity<MovimientoTarjetaEntradaDTO> crearMovimientoTarjetaDebito(@Valid @RequestBody MovimientoTarjetaEntradaDTO dto) {
        movimientoTarjetaServicesImpl.crearMovimientoDebito(dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoTarjetaSalidaDTO>> listarMovimientos() {
        return ResponseEntity.ok(movimientoTarjetaServicesImpl.listarMovimientos());
    }

    @GetMapping("/{tarjetaId}")
    public ResponseEntity<List<MovimientoTarjetaSalidaDTO>> listarMovimientosPorTarjeta(@PathVariable Long tarjetaId) {
        return ResponseEntity.ok(movimientoTarjetaServicesImpl.listarMovimientosPorTarjeta(tarjetaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoTarjetaSalidaDTO> buscarMovimientoPorId(@Valid Long id) {
        return ResponseEntity.ok(movimientoTarjetaServicesImpl.buscarMovimientoPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoTarjetaServicesImpl.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }


}
