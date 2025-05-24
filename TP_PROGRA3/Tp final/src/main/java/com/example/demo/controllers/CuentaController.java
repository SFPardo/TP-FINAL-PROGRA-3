package com.example.demo.controllers;

import com.example.demo.dto.CuentaDTO;
import com.example.demo.entities.Cuenta;
import com.example.demo.services.CuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaDTO dto) {
        cuentaService.crearCuenta(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{cuentaId}/alias")
    public ResponseEntity<String> actualizarAliasCuenta(@PathVariable Long cuentaId, @Valid @RequestBody String nuevoAlias) {
        if(cuentaService.actualizarAliasPorId(cuentaId, nuevoAlias)){
            return ResponseEntity.ok("Alias de cuenta actualizado exitosamente");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("El alias de la cuenta no ha sido actualizado");
        }
    }

    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<String> borrarCuenta(@PathVariable Long cuentaId) {
        cuentaService.borrarCuenta(cuentaId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{cbu}")
    public ResponseEntity<Cuenta> buscarPorCbu(@RequestParam String cbu) {
        Cuenta cuenta = cuentaService.buscarPorCbu(cbu);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Cuenta> buscarPorAlias(@RequestParam String alias) {
        Cuenta cuenta = cuentaService.buscarPorAlias(alias);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{cuentaId}/limite-sobregiro")
    public ResponseEntity<String> cambiarLimiteSobregiro(@PathVariable Long cuentaId, @Valid @RequestParam BigDecimal nuevoLimite) {
        cuentaService.cambiarLimiteSobregiro(cuentaId, nuevoLimite);
        return ResponseEntity.ok("Límite de sobregiro cambiado exitosamente");
    }

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestParam String cbuOrigen, @RequestParam String cbuDestino, @RequestParam BigDecimal monto) {
        cuentaService.transferenciaEntreCuentas(cbuOrigen, cbuDestino, monto);
        return ResponseEntity.ok("Transferencia realizada exitosamente");
    }

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestParam String alias, @RequestParam BigDecimal monto) {
        cuentaService.depositarDinero(alias, monto);
        return ResponseEntity.ok("Depósito realizado exitosamente");
    }

    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(@RequestParam String alias, @RequestParam BigDecimal monto) {
        cuentaService.retirarDinero(alias, monto);
        return ResponseEntity.ok("Retiro realizado exitosamente");
    }


}





