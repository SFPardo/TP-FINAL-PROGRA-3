package com.example.demo.dto;

import lombok.*;
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
    private double limite;
    private long cuentaId;

}
