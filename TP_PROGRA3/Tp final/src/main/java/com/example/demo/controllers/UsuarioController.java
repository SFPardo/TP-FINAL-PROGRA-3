package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entities.Usuario;
import com.example.demo.services.impl.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioServiceImpl;


    // -- Metodos GET --//
    @Operation(summary = "Obtener todos los usuarios paginados",
            description = "Solo accesible por usuarios con rol ADMIN. Devuelve una lista paginada de todos los usuarios.",
            parameters = {
                    @Parameter(name = "page", description = "Número de página (0-indexed).", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", description = "Número de elementos por página.", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sort", description = "Criterio de ordenamiento (ej. nombreUsuario,asc o usuarioId,desc).", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "nombreUsuario,asc"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista paginada de usuarios obtenida exitosamente.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class, subTypes = {UsuarioSalidaDTO.class}))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN.")
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioSalidaDTO>> obtenerTodosLosUsuariosPaginados(
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(usuarioServiceImpl.obtenerTodosLosUsuariosPaginados(pageable));
    }


    @Operation(summary = "Obtener usuario por ID",
        description = "Devuelve un usuario por su ID. Accesible solo por ADMIN.")
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioPorIdSalida(@PathVariable Long id) {
        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorId(id);
        if (usuario != null) {
            UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
            return ResponseEntity.ok(usuarioSalidaDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener usuario por username",
        description = "Devuelve un usuario por su nombre de usuario. Accesible solo por ADMIN.")
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioPorUsernameSalida(@PathVariable String username) {
        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorUsername(username);
        if (usuario != null) {
            UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());
            return ResponseEntity.ok(usuarioSalidaDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener el usuario autenticado actualmente",
            description = "Devuelve los detalles del usuario (nombre de usuario e ID) del cliente o administrador que ha iniciado sesión. No requiere ID en la URL.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario autenticado encontrado."),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado (raro si está autenticado, podría indicar problema de datos)."),
                    @ApiResponse(responseCode = "401", description = "No autorizado (falta token JWT o es inválido).")
            })
    @GetMapping("/mi-usuario")
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté autenticado (CLIENTE o ADMIN)
    public ResponseEntity<UsuarioSalidaDTO> obtenerUsuarioActual(Authentication authentication) {
        String nombreUsuario = authentication.getName();

        Usuario usuario = usuarioServiceImpl.buscarUsuarioPorUsername(nombreUsuario);
        UsuarioSalidaDTO usuarioSalidaDTO = new UsuarioSalidaDTO(usuario.getUsuarioId(), usuario.getNombreUsuario());

        return usuario != null ? ResponseEntity.ok(usuarioSalidaDTO) : ResponseEntity.notFound().build();
    }

    // -- Metodos PUT --//

//    @Operation(summary = "Actualizar usuario por ID",
//        description = "Actualiza un usuario por su ID. Accesible solo por ADMIN.")
//    @PutMapping("/id/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntradaDTO usuarioEntradaDTO) {
//        usuarioServiceImpl.actualizarUsuarioPorIdDto(id, usuarioEntradaDTO);
//        Usuario usuarioActualizado = usuarioServiceImpl.buscarUsuarioPorId(id);
//        return usuarioActualizado != null ? ResponseEntity.ok(usuarioActualizado) : ResponseEntity.notFound().build();
//    }

    @Operation(summary = "Cambiar el PIN del usuario autenticado",
            description = "Permite al usuario autenticado cambiar su PIN. Requiere el PIN actual y el nuevo PIN (confirmación incluida).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para cambiar el PIN (PIN actual, nuevo PIN, confirmar nuevo PIN)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CambiarPinDTO.class),
                            examples = @ExampleObject(name = "Ejemplo de cambio de PIN", value = "{\"pinActual\": \"1234\", \"nuevoPin\": \"5678\", \"confirmarNuevoPin\": \"5678\"}")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "PIN actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (PIN actual incorrecto o nuevo PIN no coincide)."),
                    @ApiResponse(responseCode = "401", description = "No autorizado (falta token JWT o es inválido)."),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado (raro si está autenticado) o credencial no encontrada.")
            })
    @PutMapping("/mi-pin")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cambiarMiPin(
            Authentication authentication,
            @Valid @RequestBody CambiarPinDTO cambioPinDTO) {

        String nombreUsuario = authentication.getName();

        usuarioServiceImpl.cambiarPinUsuarioAutenticado(nombreUsuario, cambioPinDTO);

        return ResponseEntity.ok("PIN actualizado exitosamente.");
    }

    @Operation(summary = "Cambiar el PIN de un usuario por ID (Solo ADMIN)",
            description = "Permite a un administrador cambiar el PIN de cualquier usuario especificado por su ID. No se requiere conocer el PIN actual del usuario.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo PIN para el usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CredencialEntradaDTO.class),
                            examples = @ExampleObject(name = "Ejemplo de nuevo PIN", value = "{\"pin\": \"9876\"}")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "PIN del usuario actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (formato del nuevo PIN inválido)."),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN."),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado o credencial no encontrada para el ID especificado.")
            })
    @PutMapping("/{usuarioId}/cambiarPin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cambiarPinDeUsuario(
            @PathVariable Long usuarioId,
            @Valid @RequestBody CredencialEntradaDTO nuevoPinDto) {

        usuarioServiceImpl.cambiarPinDeUsuarioPorAdmin(usuarioId, nuevoPinDto.getPin());

        return ResponseEntity.ok("PIN del usuario " + usuarioId + " actualizado exitosamente.");
    }



    //-- Metodos DELETE--//

    @Operation(summary = "Eliminar usuario por ID",
        description = "Elimina un usuario por su ID. Accesible solo por ADMIN.")
    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioServiceImpl.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar usuario por username",
        description = "Elimina un usuario por su nombre de usuario. Accesible solo por ADMIN.")
    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuarioPorUsername(@PathVariable String username) {
        usuarioServiceImpl.eliminarUsuarioPorUsername(username);
        return ResponseEntity.noContent().build();
    }


}
