package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CUENTA")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "tbl_movimiento_cuenta"
)

public class MovimientoCuenta extends Movimiento {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId",
            nullable = false
    )
    private Cuenta cuenta;
}
