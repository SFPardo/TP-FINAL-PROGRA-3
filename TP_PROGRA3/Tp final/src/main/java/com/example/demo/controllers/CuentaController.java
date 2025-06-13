package com.example.demo.controllers;

import com.example.demo.dto.CuentaEntradaDTO;
import com.example.demo.dto.CuentaSalidaDTO;
import com.example.demo.services.impl.CuentaServiceImpl;
import com.example.demo.services.impl.PagoProgramadoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/cuentas")
@Tag(name = "Cuentas", description = "Operaciones relacionadas con las cuentas bancarias")
@SecurityRequirement(name = "Bearer Authentication")
public class CuentaController {

    private final CuentaServiceImpl cuentaServiceImpl;
    private final PagoProgramadoServiceImpl pagoProgramadoServiceImpl;

    public CuentaController(CuentaServiceImpl cuentaServiceImpl, PagoProgramadoServiceImpl pagoProgramadoServiceImpl) {
        this.cuentaServiceImpl = cuentaServiceImpl;
        this.pagoProgramadoServiceImpl = pagoProgramadoServiceImpl;
    }

    @Operation(
            summary = "Crea una nueva cuenta bancaria",
            description = "Permite a un administrador crear una nueva cuenta, asociada a un cliente o usuario existente.",
            tags = {"Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaSalidaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para crear la cuenta",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaEntradaDTO.class))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CuentaSalidaDTO> crearCuenta(@Valid @RequestBody CuentaEntradaDTO dto) {
        CuentaSalidaDTO cuentaCreada = cuentaServiceImpl.crearCuenta(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cbu}")
                .buildAndExpand(cuentaCreada.getCbu())
                .toUri();
        return ResponseEntity.created(location).body(cuentaCreada);
    }

    @Operation(
            summary = "Actualiza el alias de una cuenta",
            description = "Permite a un administrador o al dueño de la cuenta actualizar el alias de la cuenta.",
            tags = {"Cuentas Cliente", "Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alias actualizado exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Alias de cuenta actualizado exitosamente"))),
            @ApiResponse(responseCode = "304", description = "El alias no ha sido modificado (Not Modified)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "El alias de la cuenta no ha sido actualizado"))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PatchMapping("/{cuentaId}/alias")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueño(#cuentaId, principal.username))")
    public ResponseEntity<String> actualizarAliasCuenta(
            @Parameter(description = "ID de la cuenta a actualizar", required = true, example = "1")
            @PathVariable Long cuentaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo alias de la cuenta",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "MiNuevoAlias"))
            )
            @Valid @RequestBody String nuevoAlias) {
        if(cuentaServiceImpl.actualizarAliasPorId(cuentaId, nuevoAlias)){
            return ResponseEntity.ok("Alias de cuenta actualizado exitosamente");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("El alias de la cuenta no ha sido actualizado");
        }
    }

    @Operation(
            summary = "Elimina una cuenta bancaria",
            description = "Solo un administrador puede eliminar una cuenta permanentemente.",
            tags = {"Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente (No Content)", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<String> borrarCuenta(
            @Parameter(description = "ID de la cuenta a eliminar", required = true, example = "1")
            @PathVariable Long cuentaId) {
        cuentaServiceImpl.borrarCuenta(cuentaId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Actualiza el límite de sobregiro de una cuenta",
            description = "Permite a un administrador cambiar el límite de sobregiro para una cuenta específica.",
            tags = {"Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Límite de sobregiro actualizado exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Límite de sobregiro cambiado exitosamente"))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PatchMapping("/{cuentaId}/limiteSobregiro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> actualizarLimiteSobregiro(
            @Parameter(description = "ID de la cuenta", required = true, example = "1")
            @PathVariable Long cuentaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo límite de sobregiro (valor BigDecimal)",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "number", format = "bigdecimal", example = "500.00"))
            )
            @Valid @RequestBody BigDecimal nuevoLimite) {
        cuentaServiceImpl.cambiarLimiteSobregiro(cuentaId, nuevoLimite);
        return ResponseEntity.ok("Límite de sobregiro cambiado exitosamente");
    }

    @Operation(
            summary = "Programa un débito automático para una cuenta",
            description = "Permite a un administrador programar un pago recurrente desde una cuenta.",
            tags = {"Administración", "Pagos Programados"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Débito automático programado exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Débito automático programado exitosamente"))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{cuentaId}/debitoAutomatico")
    public ResponseEntity<String> programarDebitoAutomatico(
            @Parameter(description = "ID de la cuenta desde la que se programará el débito", required = true, example = "1")
            @PathVariable Long cuentaId,
            @Parameter(description = "Monto del débito automático", required = true, example = "100.50")
            @Valid @RequestParam BigDecimal monto,
            @Parameter(description = "Descripción del débito automático", required = true, example = "Pago de alquiler")
            @Valid @RequestParam String descripcion) {
        pagoProgramadoServiceImpl.programarPagoProgramado(cuentaId, monto, descripcion);
        return ResponseEntity.ok("Débito automático programado exitosamente");
    }

    @Operation(
            summary = "Realiza la compra de dólares",
            description = "Permite a un administrador o al dueño de la cuenta de origen comprar dólares desde una cuenta en pesos.",
            tags = {"Operaciones Financieras"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra de dólares realizada exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Compra de dólares realizada exitosamente"))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos o fondos insuficientes", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuentas no encontradas", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueño(#idCuentaOrigen, principal.username))")
    @PostMapping("/comprarDolares")
    public ResponseEntity<String> comprarDolares(
            @Parameter(description = "ID de la cuenta de origen en pesos", required = true, example = "1")
            @RequestParam Long idCuentaOrigen,
            @Parameter(description = "ID de la cuenta destino en dólares", required = true, example = "2")
            @RequestParam Long idCuentaDolares,
            @Parameter(description = "Monto en pesos a utilizar para la compra", required = true, example = "1000.00")
            @RequestParam BigDecimal montoPesos) {
        cuentaServiceImpl.comprarDolares(idCuentaOrigen, idCuentaDolares, montoPesos);
        return ResponseEntity.ok("Compra de dólares realizada exitosamente");
    }

    @Operation(
            summary = "Realiza la venta de dólares",
            description = "Permite a un administrador o al dueño de la cuenta de dólares vender dólares a una cuenta en pesos.",
            tags = {"Operaciones Financieras"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta de dólares realizada exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Venta de dólares realizada exitosamente"))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos o fondos insuficientes", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuentas no encontradas", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueño(#idCuentaDolares, principal.username))")
    @PostMapping("/ventaDolares")
    public ResponseEntity<String> ventaDolares(
            @Parameter(description = "ID de la cuenta de origen en dólares", required = true, example = "2")
            @RequestParam Long idCuentaDolares,
            @Parameter(description = "ID de la cuenta destino en pesos", required = true, example = "1")
            @RequestParam Long idCuentaDestino,
            @Parameter(description = "Monto en dólares a vender", required = true, example = "50.00")
            @RequestParam BigDecimal montoDolares) {
        cuentaServiceImpl.ventaDolares(idCuentaDolares, idCuentaDestino, montoDolares);
        return ResponseEntity.ok("Venta de dólares realizada exitosamente");
    }

    @Operation(
            summary = "Realiza una transferencia entre cuentas",
            description = "Permite a un administrador o al dueño de la cuenta destino realizar una transferencia.",
            tags = {"Operaciones Financieras"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia realizada exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Transferencia realizada exitosamente"))),
            @ApiResponse(responseCode = "400", description = "CBU inválido, monto inválido o fondos insuficientes", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuentas no encontradas", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueñoPorCbu(#cbuDestino, principal.username))")
    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(
            @Parameter(description = "CBU de la cuenta de origen", required = true, example = "0000000100000000000000")
            @RequestParam String cbuOrigen,
            @Parameter(description = "CBU de la cuenta de destino", required = true, example = "0000000200000000000000")
            @RequestParam String cbuDestino,
            @Parameter(description = "Monto a transferir", required = true, example = "250.00")
            @RequestParam BigDecimal monto) {
        cuentaServiceImpl.transferenciaEntreCuentas(cbuOrigen, cbuDestino, monto);
        return ResponseEntity.ok("Transferencia realizada exitosamente");
    }

    @Operation(
            summary = "Realiza un depósito en una cuenta",
            description = "Permite a un administrador o al dueño de la cuenta realizar un depósito.",
            tags = {"Operaciones Financieras"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Depósito realizado exitosamente"))),
            @ApiResponse(responseCode = "400", description = "Alias inválido o monto inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueñoPorAlias(#alias, principal.username))")
    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(
            @Parameter(description = "Alias de la cuenta de destino", required = true, example = "mi.cuenta.personal")
            @RequestParam String alias,
            @Parameter(description = "Monto a depositar", required = true, example = "500.00")
            @RequestParam BigDecimal monto) {
        cuentaServiceImpl.depositarDinero(alias, monto);
        return ResponseEntity.ok("Depósito realizado exitosamente");
    }

    @Operation(
            summary = "Realiza un retiro de una cuenta",
            description = "Permite a un administrador o al dueño de la cuenta realizar un retiro.",
            tags = {"Operaciones Financieras"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retiro realizado exitosamente",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Retiro realizado exitosamente"))),
            @ApiResponse(responseCode = "400", description = "Alias inválido, monto inválido o fondos insuficientes", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueñoPorAlias(#alias, principal.username))")
    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(
            @Parameter(description = "Alias de la cuenta de origen", required = true, example = "mi.cuenta.personal")
            @RequestParam String alias,
            @Parameter(description = "Monto a retirar", required = true, example = "100.00")
            @RequestParam BigDecimal monto) {
        cuentaServiceImpl.retirarDinero(alias, monto);
        return ResponseEntity.ok("Retiro realizado exitosamente");
    }

    @Operation(
            summary = "Busca una cuenta por su CBU",
            description = "Permite a un administrador buscar una cuenta específica utilizando su CBU.",
            tags = {"Búsqueda", "Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaSalidaDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/porCbu/{cbu}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorCbu(
            @Parameter(description = "CBU de la cuenta a buscar", required = true, example = "0000000100000000000000")
            @PathVariable String cbu) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorCbu(cbu);
        return ResponseEntity.ok(cuenta);
    }

    @Operation(
            summary = "Busca una cuenta por su Alias",
            description = "Permite a un administrador buscar una cuenta específica utilizando su alias.",
            tags = {"Búsqueda", "Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaSalidaDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/porAlias/{alias}")
    public ResponseEntity<CuentaSalidaDTO> buscarPorAlias(
            @Parameter(description = "Alias de la cuenta a buscar", required = true, example = "mi.cuenta.ahorros")
            @PathVariable String alias) {
        CuentaSalidaDTO cuenta = cuentaServiceImpl.buscarPorAlias(alias);
        return ResponseEntity.ok(cuenta);
    }

    @Operation(
            summary = "Lista todas las cuentas bancarias",
            description = "Permite a un administrador obtener una lista de todas las cuentas registradas en el sistema.",
            tags = {"Búsqueda", "Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaSalidaDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado (requiere rol ADMIN)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<CuentaSalidaDTO> listarCuentas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return cuentaServiceImpl.listarCuentas(pageable);
    }

    @Operation(
            summary = "Lista las cuentas de un usuario específico",
            description = "Permite a un administrador o al mismo cliente listar las cuentas asociadas a un ID de usuario.",
            tags = {"Búsqueda", "Cuentas Cliente", "Administración"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas del usuario obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaSalidaDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueñoPorUsuarioId(#usuarioId, principal.username))")
    @GetMapping("/porUsuario/{usuarioId}")
    public Page<CuentaSalidaDTO> listarCuentasUsuario(@Parameter(description = "ID del usuario cuyas cuentas se desean listar", required = true, example = "1") @PathVariable Long usuarioId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return cuentaServiceImpl.listarCuentasPorUsuario(pageable, usuarioId);
    }

}





