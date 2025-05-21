package com.example.demo.entities;

import com.example.demo.entities.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_cuenta"
)
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cuentaId;
    @Column(
            name = "cbu",
            nullable = false,
            unique = true
    )
    private String cbu;
    @Column(
            name = "alias",
            nullable = false,
            unique = true
    )
    private String alias;
    private double saldo;
    private TipoCuenta tipoCuenta;
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "usuarioId"
    )
    private Usuario usuario;
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "cuenta_id",
            referencedColumnName = "cuentaId"
    )
    private List<Movimiento> movimientoList;
}
