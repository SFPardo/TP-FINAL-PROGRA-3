package com.example.demo.dto;

import com.example.demo.entities.enums.TipoMovimiento;
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
    private TipoMovimiento tipoMovimiento;
    private BigDecimal monto;
}
