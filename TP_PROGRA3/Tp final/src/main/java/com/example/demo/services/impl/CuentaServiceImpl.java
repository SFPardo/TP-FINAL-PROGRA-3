package com.example.demo.services.impl;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.MovimientoCuenta;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.enums.TipoCuenta;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.CuentaService;
import com.example.demo.services.GeneradorAliasService;
import com.example.demo.services.GeneradorCbuService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaServiceImpl implements CuentaService {
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GeneradorAliasService generadorAliasService;
    @Autowired
    private GeneradorCbuService generadorCbuService;

    @Override
    @Transactional
    public Cuenta crearCuenta(CuentaEntradaDTO dto){
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario con el Id proporcionado no existe"));
        Cuenta cuenta = Cuenta.builder()
                .usuario(usuario)
                .build();

        String cbu = generadorCbuService.generarCbu();
        cuenta.setCbu(cbu);

        String alias = generadorAliasService.generarAlias();
        cuenta.setAlias(alias);

        cuenta.setSaldo(BigDecimal.valueOf(0));

        cuenta.setTipoCuenta(dto.getTipoCuenta());

        if(noEsCuentaCorriente(cuenta.getUsuario().getUsuarioId())){
            cuenta.setLimiteSobregiro(BigDecimal.valueOf(0));
        }else{
            cuenta.setLimiteSobregiro(BigDecimal.valueOf(50000));
        }

        return cuentaRepository.save(cuenta);
    }

    public boolean noEsCuentaCorriente(Long usuarioId) {
        return cuentaRepository.findById(usuarioId)
                .map(cuenta -> !cuenta.getTipoCuenta().equals(TipoCuenta.CORRIENTE))
                .orElse(true);
    }

    @Override
    @Transactional
    public boolean actualizarAliasPorId(Long cuentaId, String nuevoAlias){
        if(cuentaRepository.findByAlias(nuevoAlias).isPresent()){
            throw new IllegalArgumentException("El alias ingresado ya se encuentra en uso");
        }
        int filasAfectadas = cuentaRepository.actualizarAliasPorId(cuentaId, nuevoAlias);
        return filasAfectadas > 0;
    }

    @Override
    @Transactional
    public void borrarCuenta(Long cuentaId){
        if(cuentaId == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if(cuentaRepository.findById(cuentaId).isEmpty()){
            throw new IllegalArgumentException("No existe ninguna cuenta con el id ingresado");
        }
        if(cuentaRepository.findById(cuentaId).get().getSaldo().compareTo(BigDecimal.ZERO) != 0){
            throw new IllegalArgumentException("No se puede eliminar una cuenta con saldo, tanto positivo como negativo");
        }
        cuentaRepository.deleteById(cuentaId);
    }

    @Override
    public CuentaSalidaDTO buscarPorCbu(String cbu){
        if (cbu == null || cbu.isBlank()) {
            throw new IllegalArgumentException("El CBU no puede ser nulo o vacío.");
        }
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByCbu(cbu);
        if (cuentaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta con el CBU: " + cbu);
        }
        return CuentaSalidaDTO.builder()
                .cuentaId(cuentaOptional.get().getCuentaId())
                .cbu(cuentaOptional.get().getCbu())
                .alias(cuentaOptional.get().getAlias())
                .saldo(cuentaOptional.get().getSaldo())
                .tipoCuenta(cuentaOptional.get().getTipoCuenta())
                .fechaCreacion(cuentaOptional.get().getFechaCreacion())
                .usuarioId(cuentaOptional.get().getUsuario().getUsuarioId())
                .build();
    }

    @Override
    public CuentaSalidaDTO buscarPorAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias no puede ser nulo o vacío.");
        }
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByAlias(alias);
        if (cuentaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta con el alias: " + alias);
        }
        return CuentaSalidaDTO.builder()
                .cuentaId(cuentaOptional.get().getCuentaId())
                .cbu(cuentaOptional.get().getCbu())
                .alias(cuentaOptional.get().getAlias())
                .saldo(cuentaOptional.get().getSaldo())
                .tipoCuenta(cuentaOptional.get().getTipoCuenta())
                .fechaCreacion(cuentaOptional.get().getFechaCreacion())
                .usuarioId(cuentaOptional.get().getUsuario().getUsuarioId())
                .build();
    }

    @Override
    @Transactional
    public void cambiarLimiteSobregiro(Long cuentaId, BigDecimal nuevoLimite) {
        if (cuentaId == null || nuevoLimite == null) {
            throw new IllegalArgumentException("El id de la cuenta y el nuevo límite no pueden ser nulos.");
        }
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el id proporcionado."));
        if(!cuenta.getTipoCuenta().equals(TipoCuenta.CORRIENTE)) {
            throw new IllegalArgumentException("Solo las cuentas corrientes pueden tener limite de sobregiro");
        }
        if (nuevoLimite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El nuevo límite no puede ser negativo.");
        }

        cuenta.setLimiteSobregiro(nuevoLimite);
        cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void transferenciaEntreCuentas(String cbuOrigen, String cbuDestino, BigDecimal monto) {
        if (cbuOrigen == null || cbuDestino == null || monto == null) {
            throw new IllegalArgumentException("El CBU de origen, el CBU de destino y el monto no pueden ser nulos.");
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        if (cbuOrigen.equals(cbuDestino)) {
            throw new IllegalArgumentException("No se puede transferir dinero a la misma cuenta de origen.");
        }

        Cuenta cuentaOrigen = cuentaRepository.findByCbu(cbuOrigen)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el CBU proporcionado."));
        Cuenta cuentaDestino = cuentaRepository.findByCbu(cbuDestino)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el CBU proporcionado."));

        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta de origen.");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));

        MovimientoCuenta salida = MovimientoCuenta.builder()
                .monto(monto)
                .descripcion("Transferencia a " + cuentaDestino.getAlias())
                .cuenta(cuentaOrigen)
                .build();
        MovimientoCuenta entrada = MovimientoCuenta.builder()
                .monto(monto)
                .descripcion("Transferencia de " + cuentaOrigen.getAlias())
                .cuenta(cuentaDestino)
                .build();

        cuentaOrigen.addMovimiento(salida);
        cuentaDestino.addMovimiento(entrada);
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);
    }

    @Override
    @Transactional
    public void retirarDinero(String alias, BigDecimal monto) {
        if (alias == null || monto == null) {
            throw new IllegalArgumentException("El alias y el monto no pueden ser nulos.");
        }
        Cuenta cuenta = cuentaRepository.findByAlias(alias)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el id proporcionado."));

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta.");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));

        MovimientoCuenta movimiento = MovimientoCuenta.builder()
                .monto(monto)
                .descripcion("Extracción de dinero")
                .cuenta(cuenta)
                .build();

        cuenta.addMovimiento(movimiento);
        cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void depositarDinero(String alias, BigDecimal monto) {
        if (alias == null || monto == null) {
            throw new IllegalArgumentException("El alias y el monto no pueden ser nulos.");
        }
        Cuenta cuenta = cuentaRepository.findByAlias(alias)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el id proporcionado."));

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(monto));

        MovimientoCuenta movimiento = MovimientoCuenta.builder()
                .monto(monto)
                .descripcion("Depósito de dinero")
                .cuenta(cuenta)
                .build();

        cuenta.addMovimiento(movimiento);
        cuentaRepository.save(cuenta);
    }

    @Override
    public List<CuentaSalidaDTO> listarCuentas(){
        List<Cuenta> cuentas = cuentaRepository.findAll();
        return cuentas.stream()
                .map(cuenta -> CuentaSalidaDTO.builder()
                        .cuentaId(cuenta.getCuentaId())
                        .cbu(cuenta.getCbu())
                        .alias(cuenta.getAlias())
                        .saldo(cuenta.getSaldo())
                        .tipoCuenta(cuenta.getTipoCuenta())
                        .fechaCreacion(cuenta.getFechaCreacion())
                        .usuarioId(cuenta.getUsuario().getUsuarioId())
                        .build())
                .toList();
    }

    @Override
    public List<CuentaSalidaDTO> listarCuentasPorUsuario(Long usuarioId){
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        List<Cuenta> cuentas = cuentaRepository.findByUsuarioId(usuarioId);

        return cuentas.stream()
                .map(cuenta -> CuentaSalidaDTO.builder()
                        .cuentaId(cuenta.getCuentaId())
                        .cbu(cuenta.getCbu())
                        .alias(cuenta.getAlias())
                        .saldo(cuenta.getSaldo())
                        .tipoCuenta(cuenta.getTipoCuenta())
                        .fechaCreacion(cuenta.getFechaCreacion())
                        .usuarioId(cuenta.getUsuario().getUsuarioId())
                        .build())
                .toList();
    }
}
