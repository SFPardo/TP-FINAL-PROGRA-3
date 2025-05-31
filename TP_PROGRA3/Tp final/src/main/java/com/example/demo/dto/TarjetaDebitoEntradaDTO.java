package com.example.demo.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaDebitoEntradaDTO {
    private String numero;
    private LocalDate vencimiento;
    private int codigoSeguridad;
    private String marca;
    private long cuentaId;
}
