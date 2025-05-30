package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("DEBITO")
@Getter
@Setter
@ToString
@SuperBuilder
@Table(
        name = "tbl_tarjeta_debito"
)
public class TarjetaDebito extends Tarjeta{
}
