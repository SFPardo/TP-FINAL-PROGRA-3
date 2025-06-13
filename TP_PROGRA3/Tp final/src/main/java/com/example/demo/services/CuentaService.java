package com.example.demo.services;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.entities.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    CuentaSalidaDTO crearCuenta(CuentaEntradaDTO dto);
    boolean actualizarAliasPorId(Long cuentaId, String nuevoAlias);
    void borrarCuenta(Long cuentaId);
    CuentaSalidaDTO buscarPorCbu(String cbu);
    CuentaSalidaDTO buscarPorAlias(String alias);
    void cambiarLimiteSobregiro(Long cuentaId, BigDecimal nuevoLimite);
    void transferenciaEntreCuentas(String cbuOrigen, String cbuDestino, BigDecimal monto);
    void retirarDinero(String alias, BigDecimal monto);
    void depositarDinero(String alias, BigDecimal monto);
    Page<CuentaSalidaDTO> listarCuentasPorUsuario(Pageable pageable, Long usuarioId);
    Page<CuentaSalidaDTO> listarCuentas(Pageable pageable);
    void comprarDolares(Long idCuentaOrigen, Long idCuentaDolares, BigDecimal montoPesos);
    void ventaDolares(Long idCuentaDolares, Long idCuentaDestino, BigDecimal montoDolares);
}
