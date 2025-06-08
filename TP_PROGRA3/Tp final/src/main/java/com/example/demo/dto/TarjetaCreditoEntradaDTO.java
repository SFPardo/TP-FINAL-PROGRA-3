package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaCreditoEntradaDTO {
    private String numero;
    private LocalDate vencimiento;
    private int codigoSeguridad;
    private String marca;
    @Positive
    private BigDecimal limite;
    @NotNull
    private long cuentaId;
}
