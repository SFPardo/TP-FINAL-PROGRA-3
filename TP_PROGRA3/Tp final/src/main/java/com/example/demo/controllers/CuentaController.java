package com.example.demo.controllers;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.services.CuentaService;
import com.example.demo.services.PagoProgramadoService;
import com.example.demo.services.impl.CuentaServiceImpl;
import com.example.demo.services.impl.PagoProgramadoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaServiceImpl cuentaServiceImpl;
    private final PagoProgramadoServiceImpl pagoProgramadoServiceImpl;

    public CuentaController(CuentaServiceImpl cuentaServiceImpl, PagoProgramadoServiceImpl pagoProgramadoServiceImpl) {
        this.cuentaServiceImpl = cuentaServiceImpl;
        this.pagoProgramadoServiceImpl = pagoProgramadoServiceImpl;
    }

    @PostMapping
    public ResponseEntity<CuentaSalidaDTO> crearCuenta(@Valid @RequestBody CuentaEntradaDTO dto) {
        CuentaSalidaDTO cuentaCreada = cuentaServiceImpl.crearCuenta(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cbu}")
                .buildAndExpand(cuentaCreada.getCbu())
                .toUri();
        return ResponseEntity.created(location).body(cuentaCreada);
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
    public ResponseEntity<String> programarDebitoAutomatico(@PathVariable Long cuentaId, @Valid @RequestParam BigDecimal monto, @Valid @RequestParam String descripcion) {
        pagoProgramadoServiceImpl.programarPagoProgramado(cuentaId, monto, descripcion);
        return ResponseEntity.ok("Débito automático programado exitosamente");
    }

    @PostMapping("/comprarDolares")
    public ResponseEntity<String> comprarDolares(@RequestParam Long idCuentaOrigen, @RequestParam Long idCuentaDolares, @RequestParam BigDecimal montoPesos) {
        cuentaServiceImpl.comprarDolares(idCuentaOrigen, idCuentaDolares, montoPesos);
        return ResponseEntity.ok("Compra de dólares realizada exitosamente");
    }

    @PostMapping("/ventaDolares")
    public ResponseEntity<String> ventaDolares(@RequestParam Long idCuentaDolares, @RequestParam Long idCuentaDestino, @RequestParam BigDecimal montoDolares) {
        cuentaServiceImpl.ventaDolares(idCuentaDolares, idCuentaDestino, montoDolares);
        return ResponseEntity.ok("Venta de dólares realizada exitosamente");
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

    @GetMapping("/porCbu/{cbu}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorCbu(@PathVariable String cbu) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorCbu(cbu);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/porAlias/{alias}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorAlias(@PathVariable String alias) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorAlias(alias);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping
    public ResponseEntity<List<CuentaSalidaDTO>> listarCuentas(){
        List<CuentaSalidaDTO> salida = cuentaServiceImpl.listarCuentas();
        return ResponseEntity.ok(salida);
    }

    @GetMapping("/porUsuario/{usuarioId}")
    public ResponseEntity<List<CuentaSalidaDTO>> listarCuentasUsuario(@PathVariable Long usuarioId){
        List <CuentaSalidaDTO> salida = cuentaServiceImpl.listarCuentasPorUsuario(usuarioId);
        return ResponseEntity.ok(salida);
    }

}





