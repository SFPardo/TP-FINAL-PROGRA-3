package com.example.demo.entities;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class Domicilio {
    private String provincia;
    private String ciudad;
    private String calle;
    private int altura;
}
