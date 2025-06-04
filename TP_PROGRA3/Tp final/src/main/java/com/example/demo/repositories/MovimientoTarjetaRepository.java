package com.example.demo.repositories;

import com.example.demo.entities.MovimientoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MovimientoTarjetaRepository extends JpaRepository<MovimientoTarjeta, Long> {
    List<MovimientoTarjeta> findByTarjeta_TarjetaIdOrderByFechaDesc(Long tarjetaId);
}
