package com.example.demo.controllers;

import com.example.demo.dto.LogInDTO;
import com.example.demo.dto.RegisterDTO; // Aunque no se usa en el método actual, se mantiene para contexto
import com.example.demo.entities.Usuario; // Aunque no se usa directamente en el controlador, puede ser relevante para DTOs
import com.example.demo.services.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;


@SecurityScheme(
        name = "bearerAuth", // Nombre que usarás en @SecurityRequirement
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Autenticación JWT usando un Bearer Token"
)
@RestController
@RequestMapping("/autenticador")
@Tag(name = "Autenticación", description = "Endpoints para registro y autenticación de usuarios")
public class AutenticadorController {

    @Autowired
    private AuthServiceImpl authService;

    @Schema(description = "Respuesta de autenticación JWT")
    public static class JwtAuthResponse {
        @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c3VhcmlvMSIsImlhd... (token completo)")
        public String accessToken;
        @Schema(description = "Tipo de token (siempre 'Bearer')", example = "Bearer")
        public String tokenType = "Bearer";

        public JwtAuthResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }


    @Operation(summary = "Autenticar usuario y obtener token JWT",
            description = "Permite a un usuario iniciar sesión con su nombre de usuario y PIN para obtener un token de acceso JWT. Este token debe ser incluido en las cabeceras de las solicitudes protegidas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario para iniciar sesión",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LogInDTO.class),
                            examples = @ExampleObject(name = "Ejemplo de solicitud de login", value = "{\"nombreUsuario\": \"usuario1\", \"pin\": \"1234\"}")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa. Se devuelve el token JWT.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtAuthResponse.class),
                                    examples = @ExampleObject(name = "Ejemplo de respuesta exitosa", value = "{\"accessToken\": \"eyJhbGciOiJIUzUxMiJ9...\", \"tokenType\": \"Bearer\"}")
                            )),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. campos de LogInDTO nulos o vacíos)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Ejemplo de error de validación", value = "{\"timestamp\": \"2023-10-27T10:00:00.000+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"El nombre de usuario no puede estar vacío\", \"path\": \"/autenticador/login\"}")) // Puedes personalizar tu estructura de error
                    ),
                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas (nombre de usuario o PIN incorrectos)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "Ejemplo de error de credenciales", value = "{\"timestamp\": \"2023-10-27T10:00:00.000+00:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Credenciales inválidas\", \"path\": \"/autenticador/login\"}"))) // Puedes personalizar tu estructura de error
            })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> autenticarUsuario(@Valid @RequestBody LogInDTO loginDto) {
        String token = authService.autenticarUsuario(loginDto.getNombreUsuario(), loginDto.getPin());
        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @PatchMapping("/cambiarPin") // Nota: La ruta es /cambiarPin
    @Operation(
            summary = "Cambiar el PIN de un usuario",
            description = "Permite a un usuario (o a un administrador) cambiar el PIN de una cuenta de usuario específica. " +
                    "Se requiere el ID del usuario y el nuevo PIN. " +
                    "La autorización está configurada para permitir que un ADMIN cambie cualquier PIN, " +
                    "o que un usuario autenticado cambie su propio PIN.",
            tags = { "Autenticación" }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "PIN cambiado exitosamente (No Content - Sin Contenido)"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida: el 'usuarioId' o 'nuevoPin' son incorrectos o faltantes."),
            @ApiResponse(responseCode = "401", description = "No autorizado: No se proporcionó un token de autenticación válido."),
            @ApiResponse(responseCode = "403", description = "Prohibido: El usuario autenticado no tiene permisos para cambiar el PIN de esta cuenta " +
                    "(no es ADMIN y no coincide con el 'usuarioId' loggeado)."),
            @ApiResponse(responseCode = "404", description = "No encontrado: El 'usuarioId' proporcionado no corresponde a ningún usuario existente."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> cambiarPinUsuario(
            @Parameter(
                    description = "ID del usuario cuyo PIN se va a cambiar. Debe ser un ID de usuario válido existente en el sistema.",
                    required = true,
                    example = "1"
            )
            @RequestParam Long usuarioId,
            @Parameter(
                    description = "El nuevo PIN que se asignará al usuario. " +
                            "Considerar políticas de complejidad (min. longitud, caracteres especiales, etc.).",
                    required = true,
                    example = "MiPinSuperSeguro123!"
            )
            @RequestParam String nuevoPin) {
        authService.cambiarPinUsuario(usuarioId, nuevoPin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}