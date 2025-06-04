package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("TARJETA")
@Getter
@Setter
//@ToString(exclude = {"tarjeta"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class MovimientoTarjeta extends Movimiento{
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "tarjeta_id",
            referencedColumnName = "tarjetaId",
            nullable = false
    )
    @JsonIgnoreProperties({"movimientoList", "cuenta"})
    private Tarjeta tarjeta;
}

