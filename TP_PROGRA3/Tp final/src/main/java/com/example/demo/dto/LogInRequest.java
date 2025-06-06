package com.example.demo.dto;

import lombok.Data;

@Data
public class LogInRequest {
    private String nombreUsuario;
    private int pin;
}
