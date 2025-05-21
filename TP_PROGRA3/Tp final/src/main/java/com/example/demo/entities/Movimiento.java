package com.example.demo.entities;
import com.example.demo.entities.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_movimiento"
)

public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clienteId;
    private double monto;
    private LocalDate fecha;
    private TipoMovimiento tipoMovimiento;
    private String Descripcion;
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId"
    )
    private Cuenta cuenta;
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "tarjeta_id",
            referencedColumnName = "tarjetaId"
    )
    private Tarjeta tarjeta;
}
