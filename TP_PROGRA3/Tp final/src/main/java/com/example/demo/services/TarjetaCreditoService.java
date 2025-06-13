package com.example.demo.services;

import com.example.demo.dto.TarjetaCreditoEntradaDTO;
import com.example.demo.dto.TarjetaCreditoSalidaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TarjetaCreditoService {
    TarjetaCreditoSalidaDTO crear(TarjetaCreditoEntradaDTO dto);
    Optional<TarjetaCreditoSalidaDTO> buscarPorId(Long id);
    Optional<TarjetaCreditoSalidaDTO> findByNumero(String numero);
    List<TarjetaCreditoSalidaDTO> listarTodas();
    TarjetaCreditoSalidaDTO actualizar(Long id, TarjetaCreditoEntradaDTO dto);
    void eliminar(Long id);
    TarjetaCreditoSalidaDTO actualizarLimite(Long id, BigDecimal limite);

    void pagarTarjeta(Long tarjetaId, double monto);
    boolean pagarConTarjeta(Long tarjetaId, double monto);
    Page<TarjetaCreditoSalidaDTO> listarPaginado(Pageable pageable);

}
