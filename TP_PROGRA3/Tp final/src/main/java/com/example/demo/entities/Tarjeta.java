package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_tarjeta", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@ToString(exclude = {"cuenta", "movimientoList"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "tbl_tarjeta"
)
public abstract class Tarjeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tarjetaId;
    @NotBlank
    private String numero;
    @Future
    private LocalDate vencimiento;
    private int codigoSeguridad;
    private boolean bloqueada;
    @NotNull
    private String marca;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId",
            nullable = false
    )
    @NotNull
    @JsonIgnoreProperties({"movimientoList", "tarjetaList"})
    private Cuenta cuenta;
    @OneToMany(
            mappedBy = "tarjeta",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonIgnoreProperties({"tarjeta"})
    private List<MovimientoTarjeta> movimientoList;
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    public void addMovimiento(MovimientoTarjeta movimiento){
        movimientoList.add(movimiento);
        movimiento.setTarjeta(this);
    }
}
