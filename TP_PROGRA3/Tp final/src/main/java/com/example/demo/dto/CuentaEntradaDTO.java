package com.example.demo.dto;

import com.example.demo.entities.enums.TipoCuenta;
import com.example.demo.validation.annotation.EnumValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaEntradaDTO {
    @NotNull(message = "El tipo de cuenta no puede ser nulo")
    @EnumValue(enumClass = TipoCuenta.class)
    private TipoCuenta tipoCuenta;
    @NotNull(message = "El id del usuario no puede ser nulo")
    @Positive(message = "El id del usuario debe ser un número positivo")
    private Long usuarioId;
}
