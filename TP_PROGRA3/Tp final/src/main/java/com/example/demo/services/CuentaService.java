package com.example.demo.services;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.entities.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    Cuenta crearCuenta(CuentaEntradaDTO dto);
    boolean noEsCuentaCorriente(Long usuarioId);
    boolean actualizarAliasPorId(Long cuentaId, String nuevoAlias);
    void borrarCuenta(Long cuentaId);
    CuentaSalidaDTO buscarPorCbu(String cbu);
    CuentaSalidaDTO buscarPorAlias(String alias);
    void cambiarLimiteSobregiro(Long cuentaId, BigDecimal nuevoLimite);
    void transferenciaEntreCuentas(String cbuOrigen, String cbuDestino, BigDecimal monto);
    void retirarDinero(String alias, BigDecimal monto);
    void depositarDinero(String alias, BigDecimal monto);
    List<CuentaSalidaDTO> listarCuentas();
    List<CuentaSalidaDTO> listarCuentasPorUsuario(Long usuarioId);
}
