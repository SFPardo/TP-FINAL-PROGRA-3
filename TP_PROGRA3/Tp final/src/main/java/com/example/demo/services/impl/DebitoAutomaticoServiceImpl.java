package com.example.demo.services.impl;

import com.example.demo.entities.Cuenta;
import com.example.demo.entities.MovimientoCuenta;
import com.example.demo.entities.enums.TipoMovimiento;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.MovimientoCuentaRepository;
import com.example.demo.services.DebitoAutomaticoService;
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
public class DebitoAutomaticoServiceImpl implements DebitoAutomaticoService {
    @Autowired
    private MovimientoCuentaRepository movimientoCuentaRepository;
    @Autowired
    private CuentaRepository cuentaRepository;

    @Override
    @Transactional
    public MovimientoCuenta programarDebitoAutomatico(Long cuentaId, BigDecimal monto, String descripcion){
        LocalDateTime retraso = LocalDateTime.now().plusSeconds(10);
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        MovimientoCuenta debitoAutomatico = MovimientoCuenta.builder()
                .cuenta(cuenta)
                .monto(monto)
                .descripcion(descripcion)
                .fecha(retraso)
                .tipoMovimiento(TipoMovimiento.PENDIENTE)
                .build();
      cuenta.addMovimiento(debitoAutomatico);
      cuentaRepository.save(cuenta);
      return debitoAutomatico;
    }

    @Override
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void procesarDebitosAutomaticos(){
        List<MovimientoCuenta> debitosPendientes = movimientoCuentaRepository.findPendientesParaEjecucion(TipoMovimiento.PENDIENTE, LocalDateTime.now());
        for (MovimientoCuenta debito : debitosPendientes) {
                ejecutarDebito(debito);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ejecutarDebito(MovimientoCuenta debito){
        Optional<Cuenta> cuenta = cuentaRepository.findById(debito.getCuenta().getCuentaId());
        if(cuenta.isPresent()){
            if(cuenta.get().getSaldo().compareTo(debito.getMonto()) >= 0) {
                cuenta.get().setSaldo(cuenta.get().getSaldo().subtract(debito.getMonto()));
                debito.setTipoMovimiento(TipoMovimiento.EJECUTADO);
                cuentaRepository.save(cuenta.get());
            } else {
                debito.setTipoMovimiento(TipoMovimiento.FALLIDO);
                movimientoCuentaRepository.save(debito);
            }
        }else {
            debito.setTipoMovimiento(TipoMovimiento.FALLIDO);
            movimientoCuentaRepository.save(debito);
        }
    }



}
