package com.example.demo.services.impl;

import com.example.demo.dto.TarjetaCreditoDTO;
import com.example.demo.entities.TarjetaCredito;
import com.example.demo.entities.Tarjeta;
import com.example.demo.repositories.TarjetaRepository;
import com.example.demo.services.TarjetaCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarjetaCreditoServiceImpl implements TarjetaCreditoService {

    @Autowired
    private TarjetaRepository repository;

    @Override
    public TarjetaCredito crear(TarjetaCreditoDTO dto) {
        TarjetaCredito tarjeta = TarjetaCredito.builder()
                .numero(dto.getNumero())
                .vencimiento(dto.getVencimiento())
                .codigoSeguridad(dto.getCodigoSeguridad())
                .bloqueada(dto.isBloqueada())
                .marca(dto.getMarca())
                .limite(dto.getLimite())
                .build();
        return (TarjetaCredito) repository.save(tarjeta);
    }

    @Override
    public Optional<TarjetaCredito> buscarPorId(Long id) {
        return repository.findById(id)
                .filter(t -> t instanceof TarjetaCredito)
                .map(t -> (TarjetaCredito) t);
    }

    @Override
    public Optional<TarjetaCredito> findByNumero(String numero) {
        return repository.findByNumero(numero)
                .filter(t -> t instanceof TarjetaCredito)
                .map(t -> (TarjetaCredito) t);
    }

    @Override
    public List<TarjetaCredito> listarTodas() {
        return repository.findAll().stream()
                .filter(t -> t instanceof TarjetaCredito)
                .map(t -> (TarjetaCredito) t)
                .collect(Collectors.toList());
    }

    @Override
    public TarjetaCredito actualizar(TarjetaCreditoDTO dto) {
        TarjetaCredito tarjeta = (TarjetaCredito) repository.findById(dto.getTarjetaId())
                .filter(t -> t instanceof TarjetaCredito)
                .orElseThrow();
        tarjeta.setLimite(dto.getLimite());
        tarjeta.setBloqueada(dto.isBloqueada());
        tarjeta.setMarca(dto.getMarca());
        tarjeta.setVencimiento(dto.getVencimiento());
        return (TarjetaCredito) repository.save(tarjeta);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void pagarTarjeta(Long tarjetaId, double monto) {
        TarjetaCredito tarjeta = (TarjetaCredito) repository.findById(tarjetaId)
                .filter(t -> t instanceof TarjetaCredito)
                .orElseThrow();
        if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Tarjeta bloqueada o vencida");
        }
        // Lógica de pago...
        repository.save(tarjeta);
    }

    @Override
    public boolean pagarConTarjeta(Long tarjetaId, double monto) {
        TarjetaCredito tarjeta = (TarjetaCredito) repository.findById(tarjetaId)
                .filter(t -> t instanceof TarjetaCredito)
                .orElseThrow();
        if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(LocalDate.now())) {
            return false;
        }
        // Lógica de pago...
        repository.save(tarjeta);
        return true;
    }
}