package com.example.demo.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaDebitoSalidaDTO {
    private long tarjetaId;
    private String numero;
    private LocalDate vencimiento;
    private boolean bloqueada;
    private String marca;
    private long cuentaId;
}
