package com.example.demo.services.impl;

import com.example.demo.dto.TarjetaDebitoDTO;
import com.example.demo.entities.TarjetaDebito;
import com.example.demo.entities.Tarjeta;
import com.example.demo.repositories.TarjetaRepository;
import com.example.demo.services.TarjetaDebitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarjetaDebitoServiceImpl implements TarjetaDebitoService {

    @Autowired
    private TarjetaRepository repository;

    @Override
    public TarjetaDebito crear(TarjetaDebitoDTO dto) {
        TarjetaDebito tarjeta = TarjetaDebito.builder()
                .numero(dto.getNumero())
                .vencimiento(dto.getVencimiento())
                .codigoSeguridad(dto.getCodigoSeguridad())
                .bloqueada(dto.isBloqueada())
                .marca(dto.getMarca())
                .build();
        return (TarjetaDebito) repository.save(tarjeta);
    }

    @Override
    public Optional<TarjetaDebito> buscarPorId(Long id) {
        return repository.findById(id)
                .filter(t -> t instanceof TarjetaDebito)
                .map(t -> (TarjetaDebito) t);
    }

    @Override
    public List<TarjetaDebito> listarTodas() {
        return repository.findAll().stream()
                .filter(t -> t instanceof TarjetaDebito)
                .map(t -> (TarjetaDebito) t)
                .collect(Collectors.toList());
    }

    @Override
    public TarjetaDebito actualizar(TarjetaDebitoDTO dto) {
        TarjetaDebito tarjeta = (TarjetaDebito) repository.findById(dto.getTarjetaId())
                .filter(t -> t instanceof TarjetaDebito)
                .orElseThrow();
        tarjeta.setBloqueada(dto.isBloqueada());
        tarjeta.setMarca(dto.getMarca());
        tarjeta.setVencimiento(dto.getVencimiento());
        return (TarjetaDebito) repository.save(tarjeta);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean retirarDinero(Long tarjetaId, double monto) {
        TarjetaDebito tarjeta = (TarjetaDebito) repository.findById(tarjetaId)
                .filter(t -> t instanceof TarjetaDebito)
                .orElseThrow();
        if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }
}