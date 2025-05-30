package com.example.demo.dto;

import com.example.demo.entities.enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaEntradaDTO {
    private TipoCuenta tipoCuenta;
    private Long usuarioId;
}
