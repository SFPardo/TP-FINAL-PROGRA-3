package com.example.demo.repositories;

import com.example.demo.entities.Cuenta;
import com.example.demo.entities.Movimiento;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.enums.TipoCuenta;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cuenta> findById(Long id);
    List<Cuenta> findByUsuario_UsuarioId(Long usuarioId);
    boolean existsByUsuarioAndTipoCuenta(Usuario usuario, TipoCuenta tipoCuenta);

    @Modifying
    @Transactional
    @Query("UPDATE Cuenta c SET c.alias = :nuevoAlias WHERE c.cuentaId = :id")
    int actualizarAliasPorId(@Param("id") Long id,@Param("nuevoAlias") String nuevoAlias);
    
}
