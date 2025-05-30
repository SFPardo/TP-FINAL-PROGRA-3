package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TarjetaCreditoDTO {
    private long tarjetaId;
    private String numero;
    private LocalDate vencimiento;
    private int codigoSeguridad;
    private boolean bloqueada;
    private String marca;
    private BigDecimal limite;
    private long cuentaId;

}
