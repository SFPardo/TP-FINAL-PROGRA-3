package com.example.demo.repositories;

import com.example.demo.entities.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    Optional<Movimiento>findByNumero(Long Long);
    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);
    List<Movimiento> findByTarjetaIdOrderByFechaDesc(Long tarjetaId);
}
