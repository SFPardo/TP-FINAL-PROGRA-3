package com.example.demo.repositories;

import com.example.demo.entities.Movimiento;
import com.example.demo.entities.MovimientoCuenta;
import com.example.demo.entities.enums.TipoMovimiento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoCuentaRepository extends JpaRepository<MovimientoCuenta, Long> {
    List<MovimientoCuenta> findByCuentaIdOrderByFechaDesc(Long cuentaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT mc FROM MovimientoCuenta mc WHERE mc.tipoMovimiento = :tipo " +
            "AND mc.fechaProgramadaEjecucion <= :fechaLimite")
    List<MovimientoCuenta> findPendientesParaEjecucion(
            @Param("tipo") TipoMovimiento tipo,
            @Param("fechaLimite") LocalDateTime fechaLimite);
}
