package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("TARJETA")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "tbl_movimiento_tarjeta"
)
public class MovimientoTarjeta extends Movimiento{
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "tarjeta_id",
            referencedColumnName = "tarjetaId"
    )
    private Tarjeta tarjeta;
}

