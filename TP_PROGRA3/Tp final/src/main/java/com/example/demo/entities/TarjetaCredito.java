package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CREDITO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "tbl_tarjeta_credito"
)
public class TarjetaCredito extends Tarjeta {
    private BigDecimal limite;
    private BigDecimal saldo;
}
