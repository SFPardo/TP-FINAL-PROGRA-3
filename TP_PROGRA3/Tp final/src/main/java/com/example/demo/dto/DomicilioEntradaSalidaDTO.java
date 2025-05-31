package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioEntradaSalidaDTO {
    private String provincia;
    private String ciudad;
    private String calle;
    private int altura;
}
