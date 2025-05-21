package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DEBITO")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_tarjeta_debito"
)
public class TarjetaDebito extends Tarjeta{
}
