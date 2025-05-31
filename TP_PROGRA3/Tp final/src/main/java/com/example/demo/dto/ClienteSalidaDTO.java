package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteSalidaDTO {
    private Long clienteId;
    private String nombre;
    private String dni;
    private String email;
    private String telefono;
    private DomicilioEntradaSalidaDTO domicilio;
    private UsuarioSalidaDTO usuario;
}
