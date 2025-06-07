package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LogInDTO {
    @NotBlank(message = "Username cannot be empty")
    private String nombreUsuario;

    @NotBlank(message = "PIN cannot be empty")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be a 4-digit number")
    private String pin;
}
