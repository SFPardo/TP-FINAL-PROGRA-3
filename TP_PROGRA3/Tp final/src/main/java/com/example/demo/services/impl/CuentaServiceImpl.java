package com.example.demo.services.impl;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.MovimientoCuenta;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.enums.TipoCuenta;
import com.example.demo.entities.enums.TipoMovimiento;
import com.example.demo.exceptions.SaldoInsuficienteException;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.CuentaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private GeneradorAliasServiceImpl generadorAliasServiceImpl;
    @Autowired
    private GeneradorCbuServiceImpl generadorCbuServiceImpl;

    @Override
    @Transactional
    public CuentaSalidaDTO crearCuenta(CuentaEntradaDTO dto){
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario con el Id proporcionado no existe"));
        if (dto.getTipoCuenta().equals(TipoCuenta.CORRIENTE)) {
            boolean hasCorrienteAccount = cuentaRepository.existsByUsuarioAndTipoCuenta(usuario, TipoCuenta.CORRIENTE);
            if (hasCorrienteAccount) {
                throw new IllegalStateException("El usuario ya tiene una cuenta corriente. Cada usuario puede tener solo una cuenta corriente.");
            }
        }
        Cuenta cuenta = Cuenta.builder()
                .usuario(usuario)
                .build();

        String cbu = generadorCbuServiceImpl.generarCbu();
        cuenta.setCbu(cbu);

        String alias = generadorAliasServiceImpl.generarAlias();
        cuenta.setAlias(alias);

        cuenta.setSaldo(BigDecimal.valueOf(0));

        cuenta.setTipoCuenta(dto.getTipoCuenta());

        if(!cuenta.getTipoCuenta().equals(TipoCuenta.CORRIENTE)){
            cuenta.setLimiteSobregiro(BigDecimal.valueOf(0));
        }else{
            cuenta.setLimiteSobregiro(BigDecimal.valueOf(50000));
        }

        cuentaRepository.save(cuenta);


        return CuentaSalidaDTO.builder()
                .cuentaId(cuenta.getCuentaId())
                .cbu(cuenta.getCbu())
                .alias(cuenta.getAlias())
                .saldo(cuenta.getSaldo())
                .tipoCuenta(cuenta.getTipoCuenta())
                .fechaCreacion(cuenta.getFechaCreacion())
                .limiteSobregiro(cuenta.getLimiteSobregiro())
                .usuarioId(usuario.getUsuarioId())
                .build();
    }

    @Override
    @Transactional
    public boolean actualizarAliasPorId(Long cuentaId, String nuevoAlias){
        String aliasLimpio = nuevoAlias.trim();
        if (aliasLimpio.startsWith("\"") && aliasLimpio.endsWith("\"")) {
            aliasLimpio = aliasLimpio.substring(1, aliasLimpio.length() - 1);
        }
        aliasLimpio = aliasLimpio.replace("\r", "").replace("\n", "");

        if(cuentaRepository.findByAlias(aliasLimpio).isPresent()){
            throw new IllegalArgumentException("El alias ingresado ya se encuentra en uso");
        }
        int filasAfectadas = cuentaRepository.actualizarAliasPorId(cuentaId, aliasLimpio);
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

        BigDecimal saldoActualOrigen = cuentaOrigen.getSaldo();
        BigDecimal saldoDespuesDeTransferenciaOrigen = saldoActualOrigen.subtract(monto);

        if (cuentaOrigen.getTipoCuenta() == TipoCuenta.CORRIENTE) {
            BigDecimal limiteSobregiro = cuentaOrigen.getLimiteSobregiro();

            BigDecimal maximoPermitidoTransferir = saldoActualOrigen.add(limiteSobregiro);

            if (monto.compareTo(maximoPermitidoTransferir) > 0) {
                throw new SaldoInsuficienteException("Monto excede el saldo disponible y el límite de sobregiro en la cuenta de origen. Máximo permitido transferir: " + maximoPermitidoTransferir);
            }
            cuentaOrigen.setSaldo(saldoDespuesDeTransferenciaOrigen);

        } else {
            if (saldoDespuesDeTransferenciaOrigen.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoInsuficienteException("Fondos insuficientes en la cuenta de origen para la transferencia. Saldo actual: " + saldoActualOrigen + ", Monto a transferir: " + monto);
            }
            cuentaOrigen.setSaldo(saldoDespuesDeTransferenciaOrigen);
        }

        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));

        MovimientoCuenta salida = MovimientoCuenta.builder()
                .monto(monto.negate())
                .descripcion("Transferencia a " + cuentaDestino.getAlias())
                .cuenta(cuentaOrigen)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
                .build();
        MovimientoCuenta entrada = MovimientoCuenta.builder()
                .monto(monto)
                .descripcion("Transferencia de " + cuentaOrigen.getAlias())
                .cuenta(cuentaDestino)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
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
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        Cuenta cuenta = cuentaRepository.findByAlias(alias)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con Alias: " + alias));

        BigDecimal saldoActual = cuenta.getSaldo();
        BigDecimal saldoDespuesDeRetiro = saldoActual.subtract(monto);

        if (cuenta.getTipoCuenta() == TipoCuenta.CORRIENTE) {
            BigDecimal limiteSobregiro = cuenta.getLimiteSobregiro();

            BigDecimal maximoPermitidoRetirar = saldoActual.add(limiteSobregiro);

            if (monto.compareTo(maximoPermitidoRetirar) > 0) {
                throw new SaldoInsuficienteException("Monto excede el saldo disponible y el límite de sobregiro. Máximo permitido retirar: " + maximoPermitidoRetirar);
            }
            cuenta.setSaldo(saldoDespuesDeRetiro);

        } else {
            if (saldoDespuesDeRetiro.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta. Intento de retiro: " + monto + ", Saldo actual: " + saldoActual);
            }
            cuenta.setSaldo(saldoDespuesDeRetiro);
        }

        MovimientoCuenta movimiento = MovimientoCuenta.builder()
                .monto(monto.negate())
                .descripcion("Extracción de dinero")
                .cuenta(cuenta)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
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
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el alias proporcionado."));

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(monto));

        MovimientoCuenta movimiento = MovimientoCuenta.builder()
                .monto(monto)
                .descripcion("Depósito de dinero")
                .cuenta(cuenta)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
                .build();

        cuenta.addMovimiento(movimiento);
        cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void comprarDolares(Long idCuentaOrigen, Long idCuentaDolares, BigDecimal montoPesos){
        if (idCuentaOrigen == null || idCuentaDolares == null || montoPesos == null) {
            throw new IllegalArgumentException("Los IDs de las cuentas y el monto no pueden ser nulos.");
        }
        if (montoPesos.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        Cuenta cuentaOrigen = cuentaRepository.findById(idCuentaOrigen)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el ID proporcionado."));
        Cuenta cuentaDolares = cuentaRepository.findById(idCuentaDolares)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta de dólares con el ID proporcionado."));
        if(cuentaDolares.getTipoCuenta() != TipoCuenta.AHORRO_DOLARES){
            throw new IllegalArgumentException("Se requiere una cuenta de ahorro en dólares para realizar la compra.");
        }
        if (cuentaOrigen.getSaldo().compareTo(montoPesos) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta de origen.");
        }
        BigDecimal montoDolares = montoPesos.divide(new BigDecimal("1167"), 2, BigDecimal.ROUND_DOWN);
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(montoPesos));
        cuentaDolares.setSaldo(cuentaDolares.getSaldo().add(montoDolares));
        MovimientoCuenta movimientoOrigen = MovimientoCuenta.builder()
                .monto(montoPesos.negate())
                .descripcion("Compra de dólares")
                .cuenta(cuentaOrigen)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
                .build();
        MovimientoCuenta movimientoDolares = MovimientoCuenta.builder()
                .monto(montoDolares)
                .descripcion("Venta de dólares")
                .cuenta(cuentaDolares)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
                .build();
        cuentaOrigen.addMovimiento(movimientoOrigen);
        cuentaDolares.addMovimiento(movimientoDolares);
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDolares);
    }

    @Override
    @Transactional
    public void ventaDolares(Long idCuentaDolares, Long idCuentaDestino, BigDecimal montoDolares){
        if (idCuentaDolares == null || idCuentaDestino == null || montoDolares == null) {
            throw new IllegalArgumentException("Los IDs de las cuentas y el monto no pueden ser nulos.");
        }
        if (montoDolares.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        Cuenta cuentaDolares = cuentaRepository.findById(idCuentaDolares)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta de dólares con el ID proporcionado."));
        Cuenta cuentaDestino = cuentaRepository.findById(idCuentaDestino)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta destino con el ID proporcionado."));
        if(cuentaDolares.getTipoCuenta() != TipoCuenta.AHORRO_DOLARES){
            throw new IllegalArgumentException("Se requiere una cuenta de ahorro en dólares para realizar la venta.");
        }
        if(cuentaDestino.getTipoCuenta() != TipoCuenta.AHORRO_PESOS) {
            throw new IllegalArgumentException("La cuenta destino debe ser una cuenta de ahorro en pesos.");
        }
        if (cuentaDolares.getSaldo().compareTo(montoDolares) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta de dólares.");
        }
        BigDecimal montoPesos = montoDolares.multiply(new BigDecimal("1167"));
        cuentaDolares.setSaldo(cuentaDolares.getSaldo().subtract(montoDolares));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(montoPesos));
        MovimientoCuenta movimientoDolares = MovimientoCuenta.builder()
                .monto(montoDolares.negate())
                .descripcion("Venta de dólares")
                .cuenta(cuentaDolares)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
                .build();
        MovimientoCuenta movimientoDestino = MovimientoCuenta.builder()
                .monto(montoPesos)
                .descripcion("Compra de dólares")
                .cuenta(cuentaDestino)
                .tipoMovimiento(TipoMovimiento.EJECUTADO)
                .build();
        cuentaDolares.addMovimiento(movimientoDolares);
        cuentaDestino.addMovimiento(movimientoDestino);
        cuentaRepository.save(cuentaDolares);
        cuentaRepository.save(cuentaDestino);
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
                        .limiteSobregiro(cuenta.getLimiteSobregiro())
                        .usuarioId(cuenta.getUsuario().getUsuarioId())
                        .build())
                .toList();
    }

    @Override
    public List<CuentaSalidaDTO> listarCuentasPorUsuario(Long usuarioId){
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        List<Cuenta> cuentas = cuentaRepository.findByUsuario_UsuarioId(usuarioId);

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

    public boolean esDueño(Long cuentaId, String nombreUsuario) {
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(cuentaId);
        if (cuentaOptional.isEmpty()) {
            return false;
        }
        Cuenta cuenta = cuentaOptional.get();
        return cuenta.getUsuario().getNombreUsuario().equals(nombreUsuario);
    }

    public boolean esDueñoPorAlias(String alias, String nombreUsuario) {
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByAlias(alias);
        if (cuentaOptional.isEmpty()) {
            return false;
        }
        Cuenta cuenta = cuentaOptional.get();
        return cuenta.getUsuario().getNombreUsuario().equals(nombreUsuario);
    }

    public boolean esDueñoPorCbu(String cbu, String nombreUsuario) {
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByCbu(cbu);
        if (cuentaOptional.isEmpty()) {
            return false;
        }
        Cuenta cuenta = cuentaOptional.get();
        return cuenta.getUsuario().getNombreUsuario().equals(nombreUsuario);
    }

    public boolean esDueñoPorUsuarioId(Long usuarioId, String nombreUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOptional.get();
        return usuario.getNombreUsuario().equals(nombreUsuario);
    }
}

