package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPinDTO {
    @NotBlank(message = "El PIN actual no puede estar vacío")
    private String pinActual;

    @NotBlank(message = "El nuevo PIN no puede estar vacío")
    @Pattern(regexp = "^\\d{4}$", message = "El nuevo PIN debe ser un número de 4 dígitos")
    private String nuevoPin;

    @NotBlank(message = "La confirmación del nuevo PIN no puede estar vacía")
    private String confirmarNuevoPin;
}
