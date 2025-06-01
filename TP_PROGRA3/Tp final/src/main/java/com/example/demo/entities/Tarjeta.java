package com.example.demo.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_tarjeta", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@ToString
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
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId",
            nullable = false
    )
    @NotNull
    private Cuenta cuenta;
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "tarjeta_id",
            referencedColumnName = "tarjetaId"
    )

    private List<MovimientoTarjeta> movimientoList;

    public void addMovimiento(MovimientoTarjeta movimiento){
        movimientoList.add(movimiento);
        movimiento.setTarjeta(this);
    }
}
