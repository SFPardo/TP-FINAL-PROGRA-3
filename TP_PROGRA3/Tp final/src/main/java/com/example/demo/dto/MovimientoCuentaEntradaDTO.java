package com.example.demo.dto;

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
    private Long cuentaId;
    private String descripcion;
    private BigDecimal monto;
}
