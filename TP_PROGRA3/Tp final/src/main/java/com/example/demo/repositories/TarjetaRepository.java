package com.example.demo.repositories;

import com.example.demo.entities.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {
    Optional<Tarjeta> findByNumero(String numero);
    List<Tarjeta> findByCuenta_CuentaId(Long cuentaId);
}