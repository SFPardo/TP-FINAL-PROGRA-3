package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("CREDITO")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_tarjeta_credito"
)
public class TarjetaCredito {
}
