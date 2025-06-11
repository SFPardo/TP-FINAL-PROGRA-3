package com.example.demo.services.impl;

import com.example.demo.dto.TarjetaDebitoEntradaDTO;
import com.example.demo.dto.TarjetaDebitoSalidaDTO;
import com.example.demo.services.UsuarioService;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.TarjetaDebito;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.TarjetaDebitoRepository;
import com.example.demo.services.TarjetaDebitoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("tarjetaDebitoServiceImpl") 
public class TarjetaDebitoServiceImpl implements TarjetaDebitoService {

    @Autowired
    private TarjetaDebitoRepository repository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TarjetaDebitoRepository tarjetaDebitoRepository;


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
    @Transactional
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
    @Transactional
    public TarjetaDebitoSalidaDTO actualizar(Long id, TarjetaDebitoEntradaDTO dto) {
        TarjetaDebito tarjeta = repository.findById(id).orElseThrow();
        tarjeta.setMarca(dto.getMarca());
        tarjeta.setVencimiento(dto.getVencimiento());
        return mapToSalidaDTO(repository.save(tarjeta));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean retirarDinero(Long tarjetaId, double monto) {
        TarjetaDebito tarjeta = repository.findById(tarjetaId)
                .filter(t -> t instanceof TarjetaDebito)
                .map(t -> (TarjetaDebito) t)
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));

        if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(java.time.LocalDate.now())) {
            return false;
        }
        Cuenta cuenta = tarjeta.getCuenta();
        BigDecimal saldoActual = cuenta.getSaldo();
        BigDecimal montoARetirar = BigDecimal.valueOf(monto);

        if (saldoActual.compareTo(montoARetirar) < 0) {
            return false;
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(java.math.BigDecimal.valueOf(monto)));
        cuentaRepository.save(cuenta);
        return true;
    }

    @Override
    @Transactional
    public boolean pagarConTarjeta(Long tarjetaId, double monto) {
        TarjetaDebito tarjeta = (TarjetaDebito) repository.findById(tarjetaId)
                .filter(t -> t instanceof TarjetaDebito)
                .orElseThrow();

        if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(LocalDate.now())) {
            return false;
        }

        Cuenta cuenta = tarjeta.getCuenta();
        BigDecimal saldoActual = cuenta.getSaldo();
        BigDecimal montoADescontar = BigDecimal.valueOf(monto);

        if (saldoActual.compareTo(montoADescontar) < 0) {
            return false;
        }

        cuenta.setSaldo(saldoActual.subtract(montoADescontar));
        cuentaRepository.save(cuenta);

        return true;
    }

    public boolean esDueño(Long tarjetaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }


        if (auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }


        return tarjetaDebitoRepository.findById(tarjetaId)
                .map(tarjeta -> {
                    String usernameDueño = tarjeta.getCuenta().getUsuario().getNombreUsuario();
                    return auth.getName().equals(usernameDueño);
                })
                .orElse(false);
    }




}