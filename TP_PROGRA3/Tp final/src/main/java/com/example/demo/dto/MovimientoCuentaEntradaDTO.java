package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MovimientoCuentaEntradaDTO {
    @NotNull(message = "El id de la cuenta no puede ser nulo")
    @Positive(message = "El id de la cuenta debe ser un número positivo")
    private Long cuentaId;
    @NotNull(message = "La descripción no puede ser nula")
    @NotBlank(message = "La descripción no puede estar en blanco")
    @NotEmpty(message = "La descripción no puede estar vacía")
    private String descripcion;
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un número positivo")
    @DecimalMin(value = "0.00", inclusive = false, message = "El monto debe ser mayor que cero")
    private BigDecimal monto;
}
