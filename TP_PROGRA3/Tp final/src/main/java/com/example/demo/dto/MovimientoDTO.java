package com.example.demo.dto;

import com.example.demo.entities.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class MovimientoDTO {
    private BigDecimal monto;
    private LocalDate fecha;
    private TipoMovimiento tipoMovimiento;
    private String descripcion;
}
