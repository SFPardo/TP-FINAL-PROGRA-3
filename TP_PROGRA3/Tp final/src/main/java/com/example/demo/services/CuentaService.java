package com.example.demo.services;

import com.example.demo.dto.CuentaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.entities.Movimiento;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.enums.TipoCuenta;
import com.example.demo.entities.enums.TipoMovimiento;
import com.example.demo.repositories.CuentaRepository;
import com.example.demo.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CuentaService {
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GeneradorAliasService generadorAliasService;
    @Autowired
    private GeneradorCbuService generadorCbuService;
    @Autowired
    private MovimientoServices movimientoServices;

    @Transactional
    public Cuenta crearCuenta(CuentaDTO dto){
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario asociado a la cuenta"));
        Cuenta cuenta = Cuenta.builder()
                .usuario(usuario)
                .build();

        String cbu = generadorCbuService.generarCbu();
        cuenta.setCbu(cbu);

        String alias = generadorAliasService.generarAlias();
        cuenta.setAlias(alias);

        cuenta.setSaldo(BigDecimal.valueOf(0));

        if(noEsCuentaCorriente(cuenta.getUsuario().getUsuarioId())){
            cuenta.setLimiteSobregiro(BigDecimal.valueOf(0));
        }else{
            cuenta.setLimiteSobregiro(BigDecimal.valueOf(50000));
        }

        switch (dto.getTipoCuenta()){
            case 1 -> cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
            case 2 -> cuenta.setTipoCuenta(TipoCuenta.AHORRO_PESOS);
            case 3 -> cuenta.setTipoCuenta(TipoCuenta.AHORRO_DOLARES);
            case 4 -> cuenta.setTipoCuenta(TipoCuenta.SUELDO);
            default -> throw new IllegalArgumentException("Tipo de cuenta no válido");
        }

        return cuentaRepository.save(cuenta);
    }

    public boolean noEsCuentaCorriente(Long usuarioId) {
        return cuentaRepository.findById(usuarioId)
                .map(cuenta -> !cuenta.getTipoCuenta().equals(TipoCuenta.CORRIENTE))
                .orElse(true);
    }

    @Transactional
    public boolean actualizarAliasPorId(Long cuentaId, String nuevoAlias){
        if(cuentaRepository.findByAlias(nuevoAlias).isPresent()){
            throw new IllegalArgumentException("El alias ingresado ya se encuentra en uso");
        }
        int filasAfectadas = cuentaRepository.actualizarAliasPorId(cuentaId, nuevoAlias);
        return filasAfectadas > 0;
    }

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

    public void MostrarCuentaPorId(Long id){
        if(cuentaRepository.findById(id).isPresent()){
            System.out.println(cuentaRepository.findById(id).get().toString());
        }
    }

    public Cuenta buscarPorCbu(String cbu){
        if (cbu == null || cbu.isBlank()) {
            throw new IllegalArgumentException("El CBU no puede ser nulo o vacío.");
        }
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByCbu(cbu);
        if (cuentaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta con el CBU: " + cbu);
        }
        return cuentaOptional.get();
    }

    public Cuenta buscarPorAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias no puede ser nulo o vacío.");
        }
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByAlias(alias);
        if (cuentaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta con el alias: " + alias);
        }
        return cuentaOptional.get();
    }

    @Transactional
    public void cambiarLimiteSobregiro(Long cuentaId, BigDecimal nuevoLimite) {
        if (cuentaId == null || nuevoLimite == null) {
            throw new IllegalArgumentException("El id de la cuenta y el nuevo límite no pueden ser nulos.");
        }
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con el id proporcionado."));

        if (nuevoLimite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El nuevo límite no puede ser negativo.");
        }

        cuentaRepository.actualizarLimiteSobregiroPorId(cuentaId, nuevoLimite);
        cuentaRepository.save(cuenta);
    }


    @Transactional
    public void transferenciaEntreCuentas(String cbuOrigen, String cbuDestino, BigDecimal monto) {
        if (cbuOrigen == null || cbuDestino == null || monto == null) {
            throw new IllegalArgumentException("El CBU de origen, el CBU de destino y el monto no pueden ser nulos.");
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        Cuenta cuentaOrigen = buscarPorCbu(cbuOrigen);
        Cuenta cuentaDestino = buscarPorCbu(cbuDestino);

        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta de origen.");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));

        Movimiento salida = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.EGRESO)
                .monto(monto)
                .descripcion("Transferencia a " + cuentaDestino.getAlias())
                .cuenta(cuentaOrigen)
                .build();
        Movimiento entrada = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.INGRESO)
                .monto(monto)
                .descripcion("Transferencia de " + cuentaOrigen.getAlias())
                .cuenta(cuentaDestino)
                .build();

        cuentaOrigen.addMovimiento(salida);
        cuentaDestino.addMovimiento(entrada);

        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);
    }

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

        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.EGRESO)
                .monto(monto)
                .descripcion("Extracción de dinero")
                .cuenta(cuenta)
                .build();

        cuenta.addMovimiento(movimiento);
    }

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

        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.INGRESO)
                .monto(monto)
                .descripcion("Depósito de dinero")
                .cuenta(cuenta)
                .build();

        cuenta.addMovimiento(movimiento);
    }
}
