package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioEntradaSalidaDTO {
    @NotBlank(message = "La provincia no puede estar vacía")
    private String provincia;
    @NotBlank(message = "La ciudad no puede estar vacía")
    private String ciudad;
    @NotBlank(message = "La calle no puede estar vacía")
    private String calle;
    private int altura;
}
