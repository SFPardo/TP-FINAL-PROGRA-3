package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_movimiento_tarjeta"
)
public class MovimientoTarjeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movimientoTarjetaId;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;
    @PrePersist
    protected void alCrear(){
        fecha = LocalDate.now();
    }
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "tarjeta_id",
            referencedColumnName = "tarjetaId"
    )
    private Tarjeta tarjeta;
}

