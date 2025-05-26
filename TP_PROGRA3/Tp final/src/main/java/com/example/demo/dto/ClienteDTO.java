package com.example.demo.dto;

import com.example.demo.entities.Domicilio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClienteDTO {
    private String nombre;
    private String dni;
    private String email;
    private String telefono;
    private Domicilio domicilio;
    private String nombreUsuario;
    private int pin;
}
