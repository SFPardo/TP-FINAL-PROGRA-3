package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class MovimientoCuentaSalidaDTO {
    private Long movimientoId;
    private Long cuentaId;
    private String descripcion;
    private BigDecimal monto;
    private LocalDate fecha;
}
