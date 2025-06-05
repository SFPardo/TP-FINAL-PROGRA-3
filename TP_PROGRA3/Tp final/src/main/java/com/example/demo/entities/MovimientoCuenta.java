package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CUENTA")
@Getter
@Setter
@ToString(exclude = {"cuenta"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder


public class MovimientoCuenta extends Movimiento {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId",
            nullable = true
    )
    @JsonIgnoreProperties({"movimientoList", "tarjetaList"})
    private Cuenta cuenta;
}
