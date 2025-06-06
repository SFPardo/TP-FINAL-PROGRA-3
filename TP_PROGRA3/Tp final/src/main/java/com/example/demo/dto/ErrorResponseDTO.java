package com.example.demo.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
@Data

@Schema(description = "DTO para respuestas de error de la API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {

    @Schema(description = "Fecha y hora en que ocurrió el error", example = "2023-10-27T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP del error", example = "400")
    private int status;

    @Schema(description = "Tipo de error", example = "Error de validación")
    private String error;

    @Schema(description = "Mensaje descriptivo del error", example = "Uno o más campos tienen errores de validación.")
    private String message;

    @Schema(description = "Ruta de la solicitud que causó el error", example = "/api/cuentas")
    private String path;

    @Schema(description = "Detalles adicionales del error, típicamente para errores de validación", example = "{ 'fieldName': 'Error message' }")
    private Map<String, String> details;

    public ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path, Map<String, String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }
}
