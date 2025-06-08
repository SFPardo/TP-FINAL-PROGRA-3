package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredencialEntradaDTO {
    @NotBlank(message = "El PIN no puede estar vacío")
    @Pattern(regexp = "^\\d{4}$", message = "El PIN debe ser un número de 4 dígitos")
    String pin;
}
