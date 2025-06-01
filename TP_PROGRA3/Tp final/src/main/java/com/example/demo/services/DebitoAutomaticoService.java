package com.example.demo.services;

import com.example.demo.entities.MovimientoCuenta;

import java.math.BigDecimal;

public interface DebitoAutomaticoService {
    MovimientoCuenta programarDebitoAutomatico(Long cuentaId, BigDecimal monto, String descripcion);
    void procesarDebitosAutomaticos();
    void ejecutarDebito(MovimientoCuenta debito);
}
