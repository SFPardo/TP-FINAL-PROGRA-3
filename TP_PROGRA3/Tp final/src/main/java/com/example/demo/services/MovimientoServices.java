package com.example.demo.services;

import com.example.demo.entities.Movimiento;
import com.example.demo.repositories.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovimientoServices {
    @Autowired

    private MovimientoRepository movimientoRepository;

    public void setId(Long id) {
        this.id = id;
    }

    public Movimiento crearMovimiento(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    public Optional<Movimiento> buscarMovimientoPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    public Optional<Movimiento> eliminarMovimientoPorId(Long id) {
        movimientoRepository.deleteById(id);
    }

    public Optional<Movimiento> eliminarMovimiento(Movimiento movimiento) {
        movimientoRepository.delete(movimiento);
    }
}
