package com.example.demo.controllers;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.services.impl.CuentaServiceImpl;
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
    private CuentaServiceImpl cuentaServiceImpl;

    @PostMapping("/crear")
    public ResponseEntity<CuentaEntradaDTO> crearCuenta(@Valid @RequestBody CuentaEntradaDTO dto) {
        cuentaServiceImpl.crearCuenta(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{cuentaId}/{alias}")
    public ResponseEntity<String> actualizarAliasCuenta(@PathVariable Long cuentaId, @Valid @RequestBody String nuevoAlias) {
        if(cuentaServiceImpl.actualizarAliasPorId(cuentaId, nuevoAlias)){
            return ResponseEntity.ok("Alias de cuenta actualizado exitosamente");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("El alias de la cuenta no ha sido actualizado");
        }
    }

    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<String> borrarCuenta(@PathVariable Long cuentaId) {
        cuentaServiceImpl.borrarCuenta(cuentaId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{cbu}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorCbu(@RequestParam String cbu) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorCbu(cbu);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorAlias(@RequestParam String alias) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorAlias(alias);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{cuentaId}/limite-sobregiro")
    public ResponseEntity<String> cambiarLimiteSobregiro(@PathVariable Long cuentaId, @Valid @RequestParam BigDecimal nuevoLimite) {
        cuentaServiceImpl.cambiarLimiteSobregiro(cuentaId, nuevoLimite);
        return ResponseEntity.ok("Límite de sobregiro cambiado exitosamente");
    }

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestParam String cbuOrigen, @RequestParam String cbuDestino, @RequestParam BigDecimal monto) {
        cuentaServiceImpl.transferenciaEntreCuentas(cbuOrigen, cbuDestino, monto);
        return ResponseEntity.ok("Transferencia realizada exitosamente");
    }

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestParam String alias, @RequestParam BigDecimal monto) {
        cuentaServiceImpl.depositarDinero(alias, monto);
        return ResponseEntity.ok("Depósito realizado exitosamente");
    }

    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(@RequestParam String alias, @RequestParam BigDecimal monto) {
        cuentaServiceImpl.retirarDinero(alias, monto);
        return ResponseEntity.ok("Retiro realizado exitosamente");
    }

    //Poner en MovimientoController
    /*@GetMapping("/{alias}/movimientos")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientosCuenta(@PathVariable String alias) {
        try {
            List<MovimientoDTO> movimientos = cuentaService.listarMovimientosPorCuenta(alias);
            return ResponseEntity.ok(movimientos);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No existe una cuenta")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request
            }
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos para el alias " + alias + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }*/
    }

}





