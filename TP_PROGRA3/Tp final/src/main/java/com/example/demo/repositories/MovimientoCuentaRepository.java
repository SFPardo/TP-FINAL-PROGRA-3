package com.example.demo.repositories;

import com.example.demo.entities.Movimiento;
import com.example.demo.entities.MovimientoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoCuentaRepository extends JpaRepository<MovimientoCuenta, Long> {
    List<MovimientoCuenta> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}
