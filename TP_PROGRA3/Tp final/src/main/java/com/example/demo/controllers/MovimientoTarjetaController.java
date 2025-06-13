package com.example.demo.controllers;

import com.example.demo.dto.MovimientoTarjetaEntradaDTO;
import com.example.demo.dto.MovimientoTarjetaSalidaDTO;
import com.example.demo.services.impl.MovimientoTarjetaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importación para HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Importaciones de OpenAPI (Swagger)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController // Asegúrate de que esta anotación esté presente
@RequestMapping("/movimientoTarjeta")
@Tag(name = "Movimientos de Tarjeta", description = "API para gestionar los movimientos de tarjetas (crédito y débito)")
@SecurityRequirement(name = "bearerAuth") // Asumiendo que usas JWT Bearer Token
public class MovimientoTarjetaController {


    private final MovimientoTarjetaServiceImpl movimientoTarjetaServicesImpl;
    public MovimientoTarjetaController(MovimientoTarjetaServiceImpl movimientoTarjetaServicesImpl) {
        this.movimientoTarjetaServicesImpl = movimientoTarjetaServicesImpl;
    }


    @Operation(summary = "Registrar un movimiento de CRÉDITO en tarjeta",
            description = "Permite a un ADMIN registrar un movimiento de crédito (ej. pago de tarjeta) para una tarjeta específica.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Detalles del movimiento de crédito",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovimientoTarjetaEntradaDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movimiento de crédito creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MovimientoTarjetaEntradaDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida / Datos incorrectos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crearCredito")
    public ResponseEntity<MovimientoTarjetaEntradaDTO> crearMovimientoTarjetaCredito(@Valid @RequestBody MovimientoTarjetaEntradaDTO dto) {
        movimientoTarjetaServicesImpl.crearMovimientoCredito(dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Registrar un movimiento de DÉBITO en tarjeta",
            description = "Permite a un ADMIN registrar un movimiento de débito (ej. compra) para una tarjeta específica.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Detalles del movimiento de débito",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovimientoTarjetaEntradaDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movimiento de débito creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MovimientoTarjetaEntradaDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida / Datos incorrectos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crearDebito")
    public ResponseEntity<MovimientoTarjetaEntradaDTO> crearMovimientoTarjetaDebito(@Valid @RequestBody MovimientoTarjetaEntradaDTO dto) {
        movimientoTarjetaServicesImpl.crearMovimientoDebito(dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Listar todos los movimientos de tarjeta",
            description = "Permite a un ADMIN obtener una lista de todos los movimientos de tarjeta registrados en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de movimientos de tarjeta obtenida exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MovimientoTarjetaSalidaDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MovimientoTarjetaSalidaDTO>> listarMovimientos() {
        return ResponseEntity.ok(movimientoTarjetaServicesImpl.listarMovimientos());
    }

    @Operation(summary = "Listar movimientos por tarjeta",
            description = "Permite a un ADMIN listar todos los movimientos de cualquier tarjeta, o a un CLIENTE listar los movimientos de su propia tarjeta (si la cuenta asociada es suya).",
            parameters = @Parameter(description = "ID de la tarjeta cuyos movimientos se desean listar", required = true, example = "12345"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de movimientos de la tarjeta obtenida exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = MovimientoTarjetaSalidaDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (CLIENTE no es dueño de la cuenta asociada a la tarjeta)"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @cuentaServiceImpl.esDueño(#cuentaId, principal.username))")
    @GetMapping("/porTarjeta/{tarjetaId}")
    public ResponseEntity<List<MovimientoTarjetaSalidaDTO>> listarMovimientosPorTarjeta(@PathVariable Long tarjetaId) {
        return ResponseEntity.ok(movimientoTarjetaServicesImpl.listarMovimientosPorTarjeta(tarjetaId));
    }

    @Operation(summary = "Buscar movimiento de tarjeta por ID",
            description = "Permite a un ADMIN buscar un movimiento de tarjeta específico por su ID único.",
            parameters = @Parameter(description = "ID del movimiento de tarjeta a buscar", required = true, example = "201"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movimiento de tarjeta encontrado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MovimientoTarjetaSalidaDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Movimiento de tarjeta no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoTarjetaSalidaDTO> buscarMovimientoPorId(@Valid Long id) {
        return ResponseEntity.ok(movimientoTarjetaServicesImpl.buscarMovimientoPorId(id));
    }

    @Operation(summary = "Eliminar un movimiento de tarjeta",
            description = "Permite a un ADMIN eliminar un movimiento de tarjeta específico por su ID. ¡Esta operación debe usarse con precaución!",
            parameters = @Parameter(description = "ID del movimiento de tarjeta a eliminar", required = true, example = "202"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Movimiento de tarjeta eliminado exitosamente (No Content)"),
                    @ApiResponse(responseCode = "404", description = "Movimiento de tarjeta no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acceso prohibido (solo ADMIN)")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoTarjetaServicesImpl.eliminarMovimientoPorId(id);
        return ResponseEntity.noContent().build();
    }
}