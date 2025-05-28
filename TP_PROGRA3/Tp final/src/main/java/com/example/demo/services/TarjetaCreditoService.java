package com.example.demo.services;

import com.example.demo.dto.TarjetaCreditoDTO;
import com.example.demo.entities.Tarjeta;
import com.example.demo.entities.TarjetaCredito;
import java.util.List;
import java.util.Optional;

public interface TarjetaCreditoService {
    TarjetaCredito crear(TarjetaCreditoDTO dto);
    Optional<TarjetaCredito> buscarPorId(Long id);
    Optional<TarjetaCredito> findByNumero(String numero);
    List<TarjetaCredito> listarTodas();
    TarjetaCredito actualizar(TarjetaCreditoDTO dto);
    void eliminar(Long id);


    void pagarTarjeta(Long tarjetaId, double monto);
    boolean pagarConTarjeta(Long tarjetaId, double monto);
}