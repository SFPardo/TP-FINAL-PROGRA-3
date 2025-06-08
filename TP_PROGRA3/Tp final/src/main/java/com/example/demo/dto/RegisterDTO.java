package com.example.demo.dto;

import com.example.demo.entities.enums.TipoRol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "Username cannot be empty")
    private String nombreUsuario;

    @NotBlank(message = "PIN cannot be empty")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be a 4-digit number") // Validación de PIN de 4 dígitos
    private String pin; // Es el PIN

    @NotBlank(message = "Role cannot be empty")
    @Pattern(regexp = "^(cliente|admin)$", message = "Role must be 'cliente' or 'admin'")
    private String rol;
}
