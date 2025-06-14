package com.example.demo.services.impl;

import com.example.demo.dto.MovimientoTarjetaEntradaDTO;
import com.example.demo.dto.MovimientoTarjetaSalidaDTO;
import com.example.demo.entities.MovimientoTarjeta;
import com.example.demo.entities.TarjetaCredito;
import com.example.demo.entities.TarjetaDebito;
import com.example.demo.repositories.MovimientoTarjetaRepository;
import com.example.demo.repositories.TarjetaCreditoRepository;
import com.example.demo.repositories.TarjetaDebitoRepository;
import com.example.demo.services.MovimientoTarjetaService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoTarjetaServiceImpl implements MovimientoTarjetaService {
    @Autowired
    private MovimientoTarjetaRepository movimientoTarjetaRepository;
    @Autowired
    private TarjetaDebitoRepository tarjetaDebitoRepository;
    @Autowired
    private TarjetaCreditoRepository tarjetaCreditoRepository;

    @Override
    @Transactional
    public MovimientoTarjeta crearMovimientoDebito(MovimientoTarjetaEntradaDTO dto){
        Optional<TarjetaDebito> debito = tarjetaDebitoRepository.findByNumero(dto.getNumeroTarjeta());
        if (debito.isEmpty()) {
            throw new IllegalArgumentException("No se encontró una tarjeta de débito con el número proporcionado");
        }
        if(dto.getDescripcion() == null || dto.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        if(dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        return MovimientoTarjeta.builder()
                .tarjeta(debito.get())
                .descripcion(dto.getDescripcion())
                .monto(dto.getMonto())
                .build();

    }
    @Override
    @Transactional
    public MovimientoTarjeta crearMovimientoCredito(MovimientoTarjetaEntradaDTO dto){
        Optional<TarjetaCredito> credito = tarjetaCreditoRepository.findByNumero(dto.getNumeroTarjeta());
        if (credito.isEmpty()) {
            throw new IllegalArgumentException("No se encontró una tarjeta de crédito con el número proporcionado");
        }
        if(dto.getDescripcion() == null || dto.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        if(dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        return MovimientoTarjeta.builder()
                .tarjeta(credito.get())
                .descripcion(dto.getDescripcion())
                .monto(dto.getMonto())
                .build();

    }
    @Override
    public MovimientoTarjetaSalidaDTO buscarMovimientoPorId(Long id) {
        MovimientoTarjeta movimiento = findById(id);
        return MovimientoTarjetaSalidaDTO.builder()
                .tarjetaId(movimiento.getTarjeta().getTarjetaId())
                .descripcion(movimiento.getDescripcion())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build();
    }
    @Override
    public MovimientoTarjeta findById(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID del movimiento no puede ser nulo");
        }
        return movimientoTarjetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un movimiento con el ID proporcionado"));
    }
    @Override
    public void eliminarMovimientoPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID del movimiento no puede ser nulo");
        }
        movimientoTarjetaRepository.deleteById(id);
    }

    @Override
    public List<MovimientoTarjetaSalidaDTO> listarMovimientos() {
        List<MovimientoTarjeta> movimientos = movimientoTarjetaRepository.findAll();
        return movimientos.stream().map(movimiento -> MovimientoTarjetaSalidaDTO.builder()
                .movimientoTarjetaId(movimiento.getMovimientoId())
                .tarjetaId(movimiento.getTarjeta().getTarjetaId())
                .descripcion(movimiento.getDescripcion())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build()).toList();
    }

    @Override
    public List<MovimientoTarjetaSalidaDTO> listarMovimientosPorTarjeta(Long tarjetaId) {
        if (tarjetaId == null || tarjetaId <= 0) {
            throw new IllegalArgumentException("El número de tarjeta no puede ser nulo o negativo");
        }
        List<MovimientoTarjeta> movimientos = movimientoTarjetaRepository.findByTarjeta_TarjetaIdOrderByFechaDesc(tarjetaId);
        return movimientos.stream().map(movimiento -> MovimientoTarjetaSalidaDTO.builder()
                .movimientoTarjetaId(movimiento.getMovimientoId())
                .tarjetaId(movimiento.getTarjeta().getTarjetaId())
                .descripcion(movimiento.getDescripcion())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .build()).toList();
    }
}
