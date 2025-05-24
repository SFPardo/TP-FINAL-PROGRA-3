package com.example.demo.dto;

import com.example.demo.entities.enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CuentaDTO {
    private String cbu;
    private String alias;
    private BigDecimal saldo;
    private Integer tipoCuenta;
    private BigDecimal limiteSobregiro;
    private LocalDate fechaCreacion;
    private Long usuarioId;


}
