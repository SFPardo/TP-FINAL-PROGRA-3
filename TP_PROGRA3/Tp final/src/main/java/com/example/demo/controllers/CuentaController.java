package com.example.demo.controllers;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.services.impl.CuentaServiceImpl;
import com.example.demo.services.impl.PagoProgramadoServiceImpl;
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
    @Autowired
    private PagoProgramadoServiceImpl debitoAutomaticoServiceImpl;

    @PostMapping
    public ResponseEntity<CuentaEntradaDTO> crearCuenta(@Valid @RequestBody CuentaEntradaDTO dto) {
        cuentaServiceImpl.crearCuenta(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{cuentaId}/alias")
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

    @PatchMapping("/{cuentaId}/limiteSobregiro")
    public ResponseEntity<String> actualizarLimiteSobregiro(@PathVariable Long cuentaId, @Valid @RequestBody BigDecimal nuevoLimite) {
        cuentaServiceImpl.cambiarLimiteSobregiro(cuentaId, nuevoLimite);
        return ResponseEntity.ok("Límite de sobregiro cambiado exitosamente");
    }

    @PostMapping("/{cuentaId}/debitoAutomatico")
    public ResponseEntity<String> programarDebitoAutomatico(@PathVariable Long cuentaId, @Valid @RequestBody BigDecimal monto, @Valid @RequestBody String descripcion) {
        debitoAutomaticoServiceImpl.programarPagoProgramado(cuentaId, monto, descripcion);
        return ResponseEntity.ok("Débito automático programado exitosamente");
    }

    @PostMapping("/comprarDolares")
    public ResponseEntity<String> comprarDolares(@RequestBody Long idCuentaOrigen, @RequestBody Long idCuentaDolares, @RequestBody BigDecimal montoPesos) {
        cuentaServiceImpl.comprarDolares(idCuentaOrigen, idCuentaDolares, montoPesos);
        return ResponseEntity.ok("Compra de dólares realizada exitosamente");
    }

    @PostMapping("/ventaDolares")
    public ResponseEntity<String> ventaDolares(@RequestBody Long idCuentaDolares, @RequestBody Long idCuentaDestino, @RequestBody BigDecimal montoDolares) {
        cuentaServiceImpl.ventaDolares(idCuentaDolares, idCuentaDestino, montoDolares);
        return ResponseEntity.ok("Venta de dólares realizada exitosamente");
    }



    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestBody String cbuOrigen, @RequestBody String cbuDestino, @RequestParam BigDecimal monto) {
        cuentaServiceImpl.transferenciaEntreCuentas(cbuOrigen, cbuDestino, monto);
        return ResponseEntity.ok("Transferencia realizada exitosamente");
    }

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody String alias, @RequestBody BigDecimal monto) {
        cuentaServiceImpl.depositarDinero(alias, monto);
        return ResponseEntity.ok("Depósito realizado exitosamente");
    }

    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(@RequestBody String alias, @RequestBody BigDecimal monto) {
        cuentaServiceImpl.retirarDinero(alias, monto);
        return ResponseEntity.ok("Retiro realizado exitosamente");
    }

    @GetMapping("/{cbu}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorCbu(@PathVariable String cbu) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorCbu(cbu);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorAlias(@PathVariable String alias) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorAlias(alias);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping
    public ResponseEntity<String> listarCuentas(){
        cuentaServiceImpl.listarCuentas();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<String> listarCuentasUsuario(@Valid @RequestBody Long usuarioId){
        cuentaServiceImpl.listarCuentasPorUsuario(usuarioId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}





