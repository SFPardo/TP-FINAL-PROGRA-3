package com.example.demo.repositories;

import com.example.demo.entities.TarjetaDebito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarjetaDebitoRepository extends JpaRepository<TarjetaDebito, Long> {
    Optional<TarjetaDebito> findByNumero(String numero);
    List<TarjetaDebito> findByCuenta_CuentaId(Long cuentaId);
}