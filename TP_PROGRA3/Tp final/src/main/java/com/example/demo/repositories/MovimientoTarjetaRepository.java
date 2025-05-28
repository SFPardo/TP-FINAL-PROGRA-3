package com.example.demo.repositories;

import com.example.demo.entities.MovimientoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoTarjetaRepository extends JpaRepository<MovimientoTarjeta, Long> {
    List<MovimientoTarjeta> findByNumeroOrderByFechaDesc(String numero);
}
