package com.example.demo.services.impl;

import com.example.demo.dto.MovimientoCuentaEntradaDTO;
import com.example.demo.dto.MovimientoCuentaSalidaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.Movimiento;
import com.example.demo.entities.MovimientoCuenta;
import com.example.demo.repositories.MovimientoCuentaRepository;
import com.example.demo.services.MovimientoCuentaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MovimientoCuentaCuentaServicesImpl implements MovimientoCuentaService {

    @Autowired
    private MovimientoCuentaRepository movimientoCuentaRepository;
    @Autowired
    private CuentaServiceImpl cuentaService;


    @Override
    @Transactional
    public Movimiento crearMovimiento(MovimientoCuentaEntradaDTO dto){
        Cuenta cuenta = cuentaService.buscarPorId(dto.getCuentaId());
        if (cuenta == null) {
            throw new IllegalArgumentException("No se encontró una cuenta con el ID proporcionado");
        }
        if(dto.getDescripcion() == null || dto.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        if(dto.getTipoMovimiento() == null) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        }
        if(dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        return MovimientoCuenta.builder()
                .cuenta(cuenta)
                .descripcion(dto.getDescripcion())
                .monto(dto.getMonto())
                .build();
    }
    @Override
    public MovimientoCuentaSalidaDTO buscarMovimientoPorId(Long id) {
        MovimientoCuenta movimiento = findById(id);
        return MovimientoCuentaSalidaDTO.builder()
                .movimientoId(movimiento.getMovimientoId())
                .cuentaId(movimiento.getCuenta().getCuentaId())
                .descripcion(movimiento.getDescripcion())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build();
    }
    @Override
    public MovimientoCuenta findById(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID del movimiento no puede ser nulo");
        }
        return movimientoCuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un movimiento con el ID proporcionado"));
    }
    @Override
    @Transactional
    public void eliminarMovimientoPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID del movimiento no puede ser nulo");
        }
        movimientoCuentaRepository.deleteById(id);
    }

    @Override
    public List<MovimientoCuentaSalidaDTO> listarMovimientos() {
        List<MovimientoCuenta> movimientos = movimientoCuentaRepository.findAll();
        return movimientos.stream().map(movimiento -> MovimientoCuentaSalidaDTO.builder()
                .movimientoId(movimiento.getMovimientoId())
                .cuentaId(movimiento.getCuenta().getCuentaId())
                .descripcion(movimiento.getDescripcion())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build()).toList();
    }

    @Override
    public List<MovimientoCuentaSalidaDTO> listarMovimientosPorCuenta(Long cuentaId) {
        if(cuentaId == null) {
            throw new IllegalArgumentException("El ID de la cuenta no puede ser nulo");
        }
        List<MovimientoCuenta> movimientos = movimientoCuentaRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
        return movimientos.stream().map(movimiento -> MovimientoCuentaSalidaDTO.builder()
                .movimientoId(movimiento.getMovimientoId())
                .cuentaId(movimiento.getCuenta().getCuentaId())
                .descripcion(movimiento.getDescripcion())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build()).toList();
    }
}
