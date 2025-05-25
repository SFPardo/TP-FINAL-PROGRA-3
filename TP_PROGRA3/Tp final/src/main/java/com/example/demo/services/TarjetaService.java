package com.example.demo.services;

import com.example.demo.dto.MovimientoDTO;
import com.example.demo.entities.Tarjeta;
import com.example.demo.repositories.MovimientoRepository;
import com.example.demo.repositories.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TarjetaService {
    @Autowired
    private TarjetaRepository tarjetaRepository;
    @Autowired
    private MovimientoRepository movimientoRepository;

    public Tarjeta crearTarjeta(Tarjeta tarjeta) {
        return tarjetaRepository.save(tarjeta);
    }

    public Optional<Tarjeta> buscarPorId(Long id) {
        return tarjetaRepository.findById(id);
    }

    public Optional<Tarjeta> buscarPorNumero(String numero) {
        return tarjetaRepository.findByNumero(numero);
    }

    public List<Tarjeta> listarTodas() {
        return tarjetaRepository.findAll();
    }

    public Tarjeta actualizarTarjeta(Tarjeta tarjeta) {
        return tarjetaRepository.save(tarjeta);
    }

    public void eliminarTarjeta(Long id) {
        tarjetaRepository.deleteById(id);
    }
}
