package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DEBITO")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(
        name = "tbl_tarjeta_debito"
)
public class TarjetaDebito extends Tarjeta{
    private BigDecimal saldoDisponible;

}
