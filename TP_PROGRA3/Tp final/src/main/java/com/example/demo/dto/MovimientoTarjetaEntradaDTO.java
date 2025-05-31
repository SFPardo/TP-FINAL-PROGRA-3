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


public class MovimientoTarjetaEntradaDTO {
    @NotNull(message = "El numero de la tarjeta no puede ser nulo")
    @NotEmpty(message = "El numero de la tarjeta no puede estar vacío")
    @NotBlank(message = "El numero de la tarjeta no puede estar en blanco")
    private String numeroTarjeta;
    @NotNull(message = "La descripcion no puede ser nula")
    @NotEmpty(message = "La descripcion no puede estar vacía")
    @NotBlank(message = "La descripcion no puede estar en blanco")
    private String descripcion;
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un número positivo")
    @DecimalMin(value = "0.00", inclusive = false, message = "El monto debe ser mayor que cero")
    private BigDecimal monto;
}
