package com.example.demo.entities;
import com.example.demo.entities.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_movimiento", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public abstract class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movimientoId;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_movimiento")
    private TipoMovimiento tipoMovimiento;
    @PrePersist
    protected void alCrear(){
        fecha = LocalDateTime.now();
    }
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

}
