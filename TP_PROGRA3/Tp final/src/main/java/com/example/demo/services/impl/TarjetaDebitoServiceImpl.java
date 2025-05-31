package com.example.demo.services.impl;

import com.example.demo.dto.TarjetaDebitoEntradaDTO;
import com.example.demo.dto.TarjetaDebitoSalidaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.TarjetaDebito;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.TarjetaDebitoRepository;
import com.example.demo.services.TarjetaDebitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarjetaDebitoServiceImpl implements TarjetaDebitoService {

    @Autowired
    private TarjetaDebitoRepository repository;

    @Autowired
    private CuentaRepository cuentaRepository;

    private TarjetaDebitoSalidaDTO mapToSalidaDTO(TarjetaDebito tarjeta) {
        return TarjetaDebitoSalidaDTO.builder()
                .tarjetaId(tarjeta.getTarjetaId())
                .numero(tarjeta.getNumero())
                .vencimiento(tarjeta.getVencimiento())
                .bloqueada(tarjeta.isBloqueada())
                .marca(tarjeta.getMarca())
                .cuentaId(tarjeta.getCuenta().getCuentaId())
                .build();
    }

    @Override
    public TarjetaDebitoSalidaDTO crear(TarjetaDebitoEntradaDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(dto.getCuentaId()).orElseThrow();
        TarjetaDebito tarjeta = TarjetaDebito.builder()
                .numero(dto.getNumero())
                .vencimiento(dto.getVencimiento())
                .codigoSeguridad(dto.getCodigoSeguridad())
                .bloqueada(false)
                .marca(dto.getMarca())
                .cuenta(cuenta)
                .build();
        return mapToSalidaDTO(repository.save(tarjeta));
    }

    @Override
    public Optional<TarjetaDebitoSalidaDTO> buscarPorId(Long id) {
        return repository.findById(id).map(this::mapToSalidaDTO);
    }

    @Override
    public Optional<TarjetaDebitoSalidaDTO> findByNumero(String numero) {
        return repository.findByNumero(numero).map(this::mapToSalidaDTO);
    }

    @Override
    public List<TarjetaDebitoSalidaDTO> listarTodas() {
        return repository.findAll().stream()
                .map(this::mapToSalidaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TarjetaDebitoSalidaDTO actualizar(Long id, TarjetaDebitoEntradaDTO dto) {
        TarjetaDebito tarjeta = repository.findById(id).orElseThrow();
        tarjeta.setMarca(dto.getMarca());
        tarjeta.setVencimiento(dto.getVencimiento());
        return mapToSalidaDTO(repository.save(tarjeta));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean retirarDinero(Long tarjetaId, double monto) {
        TarjetaDebito tarjeta = repository.findById(tarjetaId).orElseThrow();
        // lógica de retiro
        return true;
    }
}