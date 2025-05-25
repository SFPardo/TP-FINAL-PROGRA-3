package com.example.demo.repositories;

import com.example.demo.entities.Cuenta;
import com.example.demo.entities.Movimiento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByCbu(String cbu);
    Optional<Cuenta> findByAlias(String alias);

    @Modifying
    @Transactional
    @Query("UPDATE Cuenta c SET c.alias = :nuevoAlias WHERE c.cuentaID = :id")
    int actualizarAliasPorId(@Param("id") Long id,@Param("nuevoAlias") String nuevoAlias);

    @Modifying
    @Transactional
    @Query("UPDATE Cuenta c SET c.saldo = :nuevoSaldo WHERE c.cuentaID = :id")
    void actualizarSaldoPorId(@Param("id") Long id, @Param("nuevoSaldo") BigDecimal nuevoSaldo);

    @Modifying
    @Transactional
    @Query("UPDATE Cuenta c SET c.limiteSobregiro = :nuevoLimite WHERE c.cuentaID = :id")
    void actualizarLimiteSobregiroPorId(@Param("id") Long id, @Param("nuevoLimite") BigDecimal nuevoLimite);
}
