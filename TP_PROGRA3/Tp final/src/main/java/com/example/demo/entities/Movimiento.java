package com.example.demo.entities;
import com.example.demo.entities.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_movimiento", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "tbl_movimiento"
)

public abstract class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movimientoId;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;
    @PrePersist
    protected void alCrear(){
        fecha = LocalDate.now();
    }

}
