package com.example.demo.services;

import com.example.demo.dto.MovimientoTarjetaEntradaDTO;
import com.example.demo.dto.MovimientoTarjetaSalidaDTO;
import com.example.demo.entities.MovimientoTarjeta;

import java.util.List;

public interface MovimientoTarjetaService {
    MovimientoTarjeta crearMovimientoDebito(MovimientoTarjetaEntradaDTO dto);
    MovimientoTarjeta crearMovimientoCredito(MovimientoTarjetaEntradaDTO dto);
    MovimientoTarjetaSalidaDTO buscarMovimientoPorId(Long id);
    MovimientoTarjeta findById(Long id);
    void eliminarMovimientoPorId(Long id);
    List<MovimientoTarjetaSalidaDTO> listarMovimientos();
    List<MovimientoTarjetaSalidaDTO> listarMovimientosPorTarjeta(String numero);
}
