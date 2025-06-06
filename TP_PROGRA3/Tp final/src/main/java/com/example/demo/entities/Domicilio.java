package com.example.demo.entities;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class Domicilio {
    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    @NotBlank(message = "La calle es obligatoria")
    private String calle;
    @Min(value = 1, message = "El número debe ser mayor o igual a 1")
    private int altura;
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
