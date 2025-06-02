package com.example.demo.services;

import com.example.demo.entities.MovimientoCuenta;

import java.math.BigDecimal;

public interface PagoProgramadoService {
    MovimientoCuenta programarPagoProgramado(Long cuentaId, BigDecimal monto, String descripcion);
    void procesarPagosProgramados();
    void ejecutarPago(MovimientoCuenta pago);
}
