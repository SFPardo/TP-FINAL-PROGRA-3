package com.example.demo.controllers;

import com.example.demo.dto.MovimientoCuentaEntradaDTO;
import com.example.demo.dto.MovimientoCuentaSalidaDTO;
import com.example.demo.services.impl.MovimientoCuentaServicesImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/movimientoCuenta")
@Tag(name = "Movimientos de Cuenta", description = "API para gestionar los movimientos de las cuentas bancarias")
@SecurityRequirement(name = "bearerAuth")
public class MovimientoCuentaController {


    private final MovimientoCuentaServicesImpl movimientoCuentaServicesImpl;
    public MovimientoCuentaController(MovimientoCuentaServicesImpl movimientoCuentaServicesImpl) {
        this.movimientoCuentaServicesImpl = movimientoCuentaServicesImpl;
    }

    @Operation(summary = "Crear un nuevo movimiento de cuenta",
            description = "Permite a un ADMIN crear un nuevo movimiento en una cuenta. No se usa para débitos o créditos directos, sino para registrar movimientos específicos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del movimiento a crear",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovimientoCuentaEntradaDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movimiento de cuenta creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MovimientoCuentaEntradaDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida / Datos de movimiento incorrectos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MovimientoCuentaEntradaDTO> crearMovimientoCuenta(@Valid @RequestBody MovimientoCuentaEntradaDTO movimiento) {
        movimientoCuentaServicesImpl.crearMovimiento(movimiento);
        return ResponseEntity.ok(movimiento);
    }

    @Operation(summary = "Listar todos los movimientos de cuenta",
            description = "Permite a un ADMIN obtener una lista de todos los movimientos registrados en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MovimientoCuentaSalidaDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MovimientoCuentaSalidaDTO>> listarMovimientos() {
        return ResponseEntity.ok(movimientoCuentaServicesImpl.listarMovimientos());
    }

    @Operation(summary = "Buscar movimiento de cuenta por ID",
            description = "Permite a un ADMIN buscar un movimiento específico por su ID único.",
            parameters = @Parameter(description = "ID del movimiento a buscar", required = true, example = "101"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movimiento encontrado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MovimientoCuentaSalidaDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoCuentaSalidaDTO> buscarMovimientoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoCuentaServicesImpl.buscarMovimientoPorId(id));
    }

    @Operation(summary = "Listar movimientos por cuenta",
            description = "Permite a un ADMIN listar todos los movimientos de cualquier cuenta, o a un CLIENTE listar los movimientos de su propia cuenta.",
            parameters = @Parameter(description = "ID de la cuenta cuyos movimientos se desean listar", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de movimientos de la cuenta obtenida exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MovimientoCuentaSalidaDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (CLIENTE no es dueño de la cuenta o ADMIN no autorizado)"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueño(#cuentaId, principal.username))")
    @GetMapping("/porCuenta/{cuentaId}")
    public ResponseEntity<List<MovimientoCuentaSalidaDTO>> listarMovimientosPorCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoCuentaServicesImpl.listarMovimientosPorCuenta(cuentaId));
    }

    @Operation(summary = "Eliminar un movimiento de cuenta",
            description = "Permite a un ADMIN eliminar un movimiento específico por su ID. Esta operación puede tener implicaciones en la contabilidad.",
            parameters = @Parameter(description = "ID del movimiento a eliminar", required = true, example = "102"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Movimiento eliminado exitosamente (No Content)"),
                    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoCuentaServicesImpl.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }
}