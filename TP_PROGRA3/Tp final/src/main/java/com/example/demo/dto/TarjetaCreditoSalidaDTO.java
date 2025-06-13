package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaCreditoSalidaDTO {
    private long tarjetaId;
    private String numero;
    private LocalDate vencimiento;
    private boolean bloqueada;
    private String marca;
    private BigDecimal limite;
    private BigDecimal saldo;
    private long cuentaId;
}
