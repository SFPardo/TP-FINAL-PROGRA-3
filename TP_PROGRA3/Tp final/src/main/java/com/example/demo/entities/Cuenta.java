package com.example.demo.entities;

import com.example.demo.entities.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"usuario", "movimientoList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_cuenta"
)
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuentaId;
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
    private BigDecimal saldo;
    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;
    private BigDecimal limiteSobregiro;
    private LocalDate fechaCreacion;
    @PrePersist
    protected void alCrear(){
        fechaCreacion = LocalDate.now();
    }
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "usuarioId"
    )
    private Usuario usuario;
    @OneToMany(
            mappedBy = "cuenta",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Movimiento> movimientoList = new ArrayList<>();

    public void addMovimiento(Movimiento movimiento) {
        movimientoList.add(movimiento);
        movimiento.setCuenta(this);
    }
}
