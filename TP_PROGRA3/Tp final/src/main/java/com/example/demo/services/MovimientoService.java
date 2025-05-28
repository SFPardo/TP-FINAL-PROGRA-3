package com.example.demo.services;

import com.example.demo.dto.MovimientoCuentaEntradaDTO;
import com.example.demo.dto.MovimientoCuentaSalidaDTO;
import com.example.demo.entities.Movimiento;

import java.util.List;

public interface MovimientoService {
    Movimiento crearMovimiento(MovimientoCuentaEntradaDTO dto);
    MovimientoCuentaSalidaDTO buscarMovimientoPorId(Long id);
    Movimiento findById(Long id);
    void eliminarMovimientoPorId(Long id);
    List<MovimientoCuentaSalidaDTO> listarMovimientos();
    List<MovimientoCuentaSalidaDTO> listarMovimientosPorCuenta(Long cuentaId);
}
