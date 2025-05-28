package com.example.demo.services;

import com.example.demo.dto.TarjetaDebitoDTO;
import com.example.demo.entities.TarjetaDebito;
import java.util.List;
import java.util.Optional;

public interface TarjetaDebitoService {
    TarjetaDebito crear(TarjetaDebitoDTO dto);
    Optional<TarjetaDebito> buscarPorId(Long id);
    List<TarjetaDebito> listarTodas();
    TarjetaDebito actualizar(TarjetaDebitoDTO dto);
    void eliminar(Long id);

    boolean retirarDinero(Long tarjetaId, double monto);
}