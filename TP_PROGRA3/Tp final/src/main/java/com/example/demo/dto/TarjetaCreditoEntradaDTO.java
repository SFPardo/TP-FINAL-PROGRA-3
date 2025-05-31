package com.example.demo.dto;

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
    private BigDecimal limite;
    private long cuentaId;
}
