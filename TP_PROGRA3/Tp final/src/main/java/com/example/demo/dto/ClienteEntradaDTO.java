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
    @NotBlank
    private String nombre;
    @Pattern(regexp = "\\d{8}")
    private String dni;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String telefono;
    @Valid
    private DomicilioEntradaSalidaDTO domicilio;
    @Valid
    private UsuarioEntradaDTO usuario;
}
