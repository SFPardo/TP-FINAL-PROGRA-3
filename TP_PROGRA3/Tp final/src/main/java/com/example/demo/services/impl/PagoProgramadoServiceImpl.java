package com.example.demo.services.impl;

import com.example.demo.entities.Cuenta;
import com.example.demo.entities.MovimientoCuenta;
import com.example.demo.entities.enums.TipoMovimiento;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.MovimientoCuentaRepository;
import com.example.demo.services.PagoProgramadoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoProgramadoServiceImpl implements PagoProgramadoService {
    @Autowired
    private MovimientoCuentaRepository movimientoCuentaRepository;
    @Autowired
    private CuentaRepository cuentaRepository;

    @Override
    @Transactional
    public MovimientoCuenta programarPagoProgramado(Long cuentaId, BigDecimal monto, String descripcion){
        LocalDateTime retraso = LocalDateTime.now().plusSeconds(10);
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        MovimientoCuenta pagoProgramado = MovimientoCuenta.builder()
                .cuenta(cuenta)
                .monto(monto)
                .descripcion(descripcion)
                .fecha(retraso)
                .tipoMovimiento(TipoMovimiento.PENDIENTE)
                .build();
      cuenta.addMovimiento(pagoProgramado);
      cuentaRepository.save(cuenta);
      return pagoProgramado;
    }

    @Override
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void procesarPagosProgramados(){
        List<MovimientoCuenta> pagosPendientes = movimientoCuentaRepository.findPendientesParaEjecucion(TipoMovimiento.PENDIENTE, LocalDateTime.now());
        if(!pagosPendientes.isEmpty()) {
            for (MovimientoCuenta debito : pagosPendientes) {
                ejecutarPago(debito);
            }
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ejecutarPago(MovimientoCuenta pago){
        Optional<Cuenta> cuenta = cuentaRepository.findById(pago.getCuenta().getCuentaId());
        if(cuenta.isPresent()){
            if(cuenta.get().getSaldo().compareTo(pago.getMonto()) >= 0) {
                cuenta.get().setSaldo(cuenta.get().getSaldo().subtract(pago.getMonto()));
                pago.setTipoMovimiento(TipoMovimiento.EJECUTADO);
                movimientoCuentaRepository.save(pago);
                cuentaRepository.save(cuenta.get());
            } else {
                pago.setTipoMovimiento(TipoMovimiento.FALLIDO);
                movimientoCuentaRepository.save(pago);
            }
        }else {
            pago.setTipoMovimiento(TipoMovimiento.FALLIDO);
            movimientoCuentaRepository.save(pago);
        }
    }



}
