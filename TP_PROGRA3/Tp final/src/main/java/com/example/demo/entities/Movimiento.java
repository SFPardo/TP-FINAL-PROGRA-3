package com.example.demo.entities;
import com.example.demo.entities.enums.TipoMovimiento;
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
        name = "tbl_movimiento"
)

public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long movimientoId;
    private BigDecimal monto;
    private LocalDate fecha;
    private TipoMovimiento tipoMovimiento;
    private String descripcion;
    @PrePersist
    protected void alCrear(){
        fecha = LocalDate.now();
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId",
            nullable = false
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
