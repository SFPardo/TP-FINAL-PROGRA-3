package com.example.demo.dto;

import com.example.demo.entities.enums.TipoCuenta;
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
public class CuentaSalidaDTO {
    private Long cuentaId;
    private String cbu;
    private String alias;
    private BigDecimal saldo;
    private BigDecimal limiteSobregiro;
    private TipoCuenta tipoCuenta;
    private LocalDate fechaCreacion;
    private Long usuarioId;
}
