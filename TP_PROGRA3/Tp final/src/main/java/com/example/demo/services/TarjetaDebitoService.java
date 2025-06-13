package com.example.demo.services;

import com.example.demo.dto.TarjetaDebitoEntradaDTO;
import com.example.demo.dto.TarjetaDebitoSalidaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TarjetaDebitoService {
    TarjetaDebitoSalidaDTO crear(TarjetaDebitoEntradaDTO dto);
    Optional<TarjetaDebitoSalidaDTO> buscarPorId(Long id);
    Optional<TarjetaDebitoSalidaDTO> findByNumero(String numero);
    List<TarjetaDebitoSalidaDTO> listarTodas();
    TarjetaDebitoSalidaDTO actualizar(Long id, TarjetaDebitoEntradaDTO dto);
    void eliminar(Long id);

    boolean retirarDinero(Long tarjetaId, double monto);
    boolean pagarConTarjeta(Long tarjetaId, double monto);
    boolean esDueño(Long tarjetaId);
    Page<TarjetaDebitoSalidaDTO> listarPaginado(Pageable pageable);

}
