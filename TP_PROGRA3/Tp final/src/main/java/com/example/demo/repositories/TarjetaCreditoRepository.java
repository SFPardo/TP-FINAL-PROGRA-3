package com.example.demo.repositories;

import com.example.demo.entities.TarjetaCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Long> {
    Optional<TarjetaCredito> findByNumero(String numero);
    List<TarjetaCredito> findByCuentaId(Long cuentaId);
}