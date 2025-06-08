package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClienteEntradaDTO {
    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String nombre;
    @NotBlank(message = "El DNI del cliente no puede estar vacío")
    @Pattern(regexp = "^\\d{7,8}$", message = "El DNI debe tener 7 u 8 dígitos")
    private String dni;
    @NotBlank(message = "El email del cliente no puede estar vacío")
    @Email(message = "Formato de email inválido")
    private String email;
    @Pattern(regexp = "\\d{10}")
    private String telefono;
    @Valid
    private DomicilioEntradaSalidaDTO domicilio;
    @Valid
    private UsuarioEntradaDTO usuario;
}
